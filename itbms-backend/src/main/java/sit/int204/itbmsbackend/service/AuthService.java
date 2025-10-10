package sit.int204.itbmsbackend.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.constant.UserRole;
import sit.int204.itbmsbackend.constant.UserStatus;
import sit.int204.itbmsbackend.constant.UserType;
import sit.int204.itbmsbackend.dto.auth.*;
import sit.int204.itbmsbackend.entity.Address;
import sit.int204.itbmsbackend.entity.RefreshToken;
import sit.int204.itbmsbackend.entity.Role;
import sit.int204.itbmsbackend.entity.User;
import sit.int204.itbmsbackend.repository.AddressRepository;
import sit.int204.itbmsbackend.util.JwtUtils;
import sit.int204.itbmsbackend.repository.RoleRepository;
import sit.int204.itbmsbackend.repository.UserRepository;
import sit.int204.itbmsbackend.security.UserPrincipal;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final ImageStorageService imageStorageService;
    private final EmailService emailService;

    @Value("${email.verification_token_expiration_hr:24}") // 24 hours default
    private int emailVerifiedExpirationHr;

    private void validateSellerField(RegisterRequest seller) {
        Map<String, String> errors = new HashMap<>();
        // Shop name
        if (seller.getShopName() == null || seller.getShopName().isBlank()) {
            errors.put("shopName", "Shop name is required.");
        }
        // Phone
        if (seller.getPhone() == null || !seller.getPhone().matches("^\\+?[0-9]{8,15}$")) {
            errors.put("phone", "Phone number must contain only digits (8–15 digits, optional + at start).");
        }
        // Bank account number
        if (seller.getBankAccountNumber() == null || !seller.getBankAccountNumber().matches("^[0-9]{6,16}$")) {
            errors.put("bankAccountNumber", "Bank account number must contain only digits (6–16 characters).");
        }
        // Bank name
        if (seller.getBankName() == null || seller.getBankName().isBlank()) {
            errors.put("bankName", "Bank name is required.");
        }
        // ID card number
        if (seller.getIdCardNumber() == null || !seller.getIdCardNumber().matches("^[0-9]{13}$")) {
            errors.put("idCardNumber", "ID Card Number must contain exactly 13 digits.");
        }
        // If any errors exist, throw exception with all
        if (!errors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid fields: " + errors.toString());
        }
    }


    @Transactional
    public User registerUser(RegisterRequest request) throws MessagingException, UnsupportedEncodingException {
        User existUser = userRepository.findOneByEmail(request.getEmail()).orElse(null);
        if (existUser != null) {
            if (UserStatus.INACTIVE.equals(existUser.getStatus()) &&
                existUser.getVerificationTokenExpiry() != null &&
                existUser.getVerificationTokenExpiry().isBefore(LocalDateTime.now())
            ) {
                userRepository.delete(existUser);   // remove expires verify-token user
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use!");
            }
        }

        // Create new user account
        User user = new User();
        user.setNickname(request.getNickname());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setUserType(request.getUserType());

        // Email verification token
        String verifiedToken = UUID.randomUUID().toString();
        user.setVerificationToken(verifiedToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(emailVerifiedExpirationHr));

        // Assign default role to user
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findOneByName(UserRole.ROLE_USER.toString())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error: Role is not found!"));
        roles.add(userRole);
        user.setRoles(roles);

        if (UserType.SELLER.equals(request.getUserType())) {
            // Validate additional seller field
            validateSellerField(request);

            // Validate upload image and save to image storage
            String idCardImageFrontFileName = null;
            String idCardImageBackFileName = null;
            if (request.getIdCardImageFront() != null && !request.getIdCardImageFront().isEmpty() &&
                    request.getIdCardImageBack() != null && !request.getIdCardImageBack().isEmpty())
            {
                idCardImageFrontFileName = imageStorageService.saveImage(request.getIdCardImageFront());
                idCardImageBackFileName = imageStorageService.saveImage(request.getIdCardImageBack());
            }
            // Adding additional field of seller
            user.setPhone(request.getPhone());
            user.setShopName(request.getShopName());
            user.setBankName(request.getBankName());
            user.setBankAccountNumber(request.getBankAccountNumber());
            user.setIdCardNumber(request.getIdCardNumber());
            user.setIdCardImageFront(idCardImageFrontFileName); // Save image name to db
            user.setIdCardImageBack(idCardImageBackFileName);
        }

        // Save user to db
        User newUser = userRepository.save(user);

        // Assign user address
        Address address = new Address();
        address.setUser(newUser);
        address.setAddressLine(user.getFullName() + " Home BangMod");
        address.setCity("Bangkok");
        address.setCountry("Thailand");
        address.setPhone("099-999-999");
        address.setPostalCode("11100");
        address.setFullName(newUser.getFullName());
        addressRepository.save(address);

        // Send email verification
        emailService.sendVerificationEmail(user.getEmail(), verifiedToken);
        return newUser;
    }

    public AuthTokenResponse authenticateUser(LoginRequest loginRequest) {
        User existUser = userRepository.findOneByEmail(loginRequest.getEmail()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found."));

        if (UserStatus.INACTIVE.equals(existUser.getStatus())) {
            if (existUser.getVerificationTokenExpiry() != null && existUser.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
                userRepository.delete(existUser);    // remove expires verify-token user
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found.");
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not verify email.");
            }
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                ));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        // Generate token
        String accessToken = jwtUtils.generateJwtToken(user);
        RefreshToken refreshTokenData = refreshTokenService.createRefreshToken(user);

        return new AuthTokenResponse(
                accessToken,
                refreshTokenData.getToken(),
                user.getId(),
                user.getNickname(),
                user.getFullName(),
                user.getEmail(),
                user.getUserType(),
                user.getRolesStr(),
                user.getIsLocked()
        );
    }

    @PostMapping("/refresh")
    public RefreshTokenResponse refreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtUtils.generateJwtToken(user);
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
                    return new RefreshTokenResponse(newRefreshToken.getToken(), accessToken);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Refresh token not found."));
    }

    public void emailVerification(String verifiedToken) {
        User user = userRepository.findOneByVerificationToken(verifiedToken).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token."));
        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The token has expires, please try register again.");
        }
        user.setStatus(UserStatus.ACTIVE);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
    }

    public UserDetailsResponse checkUserIdentity(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")
        );
        UserDetailsResponse response = new UserDetailsResponse();
        response.setId(user.getId());
        response.setNickname(user.getNickname());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRoles(user.getRolesStr());
        response.setIsLocked(user.getIsLocked());
        response.setUserType(user.getUserType());
        return response;
    }

    public void logoutUser(User user) {
        // Remove user refresh token in db
        refreshTokenService.deleteByUser(user);
    }


    /**
     * Scheduled job to delete user with expired token every 24 hour.
     */
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    @Transactional
    public void cleanupNonVerifiedUsers() {
        System.out.println(":: Cleaning up non-verified users : " + LocalDateTime.now());
        List<User> users = userRepository.findAllByExpiresToken(LocalDateTime.now());
        for (User user : users) {
            // remove id card image from storage
            if (UserType.SELLER.equals(user.getUserType()) &&
                    user.getIdCardImageFront() != null && user.getIdCardImageBack() != null)
            {
                imageStorageService.deleteImage(user.getIdCardImageFront());
                imageStorageService.deleteImage(user.getIdCardImageBack());
            }
        }
        userRepository.deleteNonVerifiedUsers(LocalDateTime.now());
    }
}
