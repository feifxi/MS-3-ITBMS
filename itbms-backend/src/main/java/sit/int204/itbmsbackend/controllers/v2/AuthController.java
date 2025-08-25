package sit.int204.itbmsbackend.controllers.v2;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.constants.UserRole;
import sit.int204.itbmsbackend.constants.UserType;
import sit.int204.itbmsbackend.dtos.common.ApiResponse;
import sit.int204.itbmsbackend.dtos.auth.*;
import sit.int204.itbmsbackend.entities.*;
import sit.int204.itbmsbackend.jwt.JwtUtils;
import sit.int204.itbmsbackend.repositories.RoleRepository;
import sit.int204.itbmsbackend.repositories.UserRepository;
import sit.int204.itbmsbackend.security.UserPrincipal;
import sit.int204.itbmsbackend.services.EmailService;
import sit.int204.itbmsbackend.services.ImageStorageService;
import sit.int204.itbmsbackend.services.RefreshTokenService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v2/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private ImageStorageService imageStorageService;
    @Autowired
    private EmailService emailService;

    @Value("${email.verification_token_expiration_hr:86400000}") // 24 hours default
    private int emailVerifiedExpirationHr;
    private final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60;

    private void validateSellerField(SellerAdditionalFieldRequest seller) {
        if (seller.getShopName() == null ||
            seller.getPhone() == null ||
            seller.getBankAccountNumber() == null ||
            seller.getBankName() == null ||
            seller.getNationalId() == null
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid seller fields.");
        }
    }

    private ResponseCookie generateCookie(String name, String value, int maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)      // Not accessible by JS
                .secure(false)        // Only send over HTTPS (set false for dev HTTP)
                .path("/itb-mshop/v2/auth/refresh") // Only send to refresh endpoint
                .maxAge(maxAge) // 7 days in seconds
//                .sameSite("Strict")  // Prevent CSRF (can also use "Lax")
                .sameSite("Lax")  // Prevent CSRF (can also use "Lax")
                .build();
    }

    @PostMapping(
            value = "/registers",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> registerUser(
            @Valid @ModelAttribute RegisterRequest registerRequest,
            @ModelAttribute SellerAdditionalFieldRequest sellerDTO) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use!");
        }

        // Create new user account
        User user = new User();
        user.setNickname(registerRequest.getNickname());
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encoder.encode(registerRequest.getPassword()));

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

        if (UserType.BUYER.equals(registerRequest.getUserType())) {
            // Create new buyer profile of user
            BuyerProfile buyerProfile = new BuyerProfile();
            buyerProfile.setUser(user);
            user.setBuyerProfile(buyerProfile);
        }
        else if (UserType.SELLER.equals(registerRequest.getUserType())) {
            // Validate additional field of seller
            validateSellerField(sellerDTO);

            // Validate upload image and save to image storage
            String nationalIdImageFrontFileName = null;
            String nationalIdImageBackFileName = null;
            if (sellerDTO.getNationalIdImageFront() != null && !sellerDTO.getNationalIdImageFront().isEmpty() &&
                sellerDTO.getNationalIdImageBack() != null && !sellerDTO.getNationalIdImageBack().isEmpty())
            {
                nationalIdImageFrontFileName = imageStorageService.saveImage(sellerDTO.getNationalIdImageFront());
                nationalIdImageBackFileName = imageStorageService.saveImage(sellerDTO.getNationalIdImageBack());
            }

            // Create new seller profile of user
            SellerProfile sellerProfile = new SellerProfile();
            sellerProfile.setUser(user);
            sellerProfile.setPhone(sellerDTO.getPhone());
            sellerProfile.setShopName(sellerDTO.getShopName());
            sellerProfile.setBankName(sellerDTO.getBankName());
            sellerProfile.setBankAccountNumber(sellerDTO.getBankAccountNumber());
            sellerProfile.setNationalId(sellerDTO.getNationalId());
            // Save image name to db
            sellerProfile.setNationalIdImageFront(nationalIdImageFrontFileName);
            sellerProfile.setNationalIdImageBack(nationalIdImageBackFileName);

            user.setSellerProfile(sellerProfile);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user type.");
        }

        // Save user to db
        userRepository.save(user);

        // Send email verification
        emailService.sendVerificationEmail(user.getEmail(), verifiedToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Buyer user registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {

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

        // Set refresh token as cookie
        ResponseCookie cookie = generateCookie("refreshToken", refreshTokenData.getToken(), COOKIE_MAX_AGE);
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(new AuthTokenResponse(
                accessToken,
                user.getId(),
                user.getNickname(),
                user.getFullName(),
                user.getEmail(),
                user.getRolesStr(),
                user.getIsLocked()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No refresh token provided");
        }
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateJwtToken(user);
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
                    ResponseCookie cookie = generateCookie("refreshToken", newRefreshToken.getToken(), COOKIE_MAX_AGE);
                    response.addHeader("Set-Cookie", cookie.toString());
                    return ResponseEntity.ok(new RefreshTokenResponse(token));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Refresh token not found."));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check if user is authenticated
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "No user is currently logged in."));
        }

        SecurityContextHolder.clearContext();

        // Remove user refresh token in db
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        refreshTokenService.deleteByUser(userPrincipal.getUser());

        // Clear refresh token cookie
        ResponseCookie clearCookie = generateCookie("refreshToken", "", 0);
        response.addHeader("Set-Cookie", clearCookie.toString());

        return ResponseEntity.ok(new ApiResponse(true, "User signed out successfully!"));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> emailVerification(@Valid @RequestBody EmailVerificationRequest request) {
        User user = userRepository.findOneByVerificationToken(request.getToken()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token or token expires."));
        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            userRepository.delete(user);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The token has expires, please try register again.");
        }
        user.setStatus("ACTIVE");
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse(true, "Email verification successfully!"));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetailsResponse> checkUserIdentity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check if user is authenticated
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();
        UserDetailsResponse response = new UserDetailsResponse();
        response.setId(user.getId());
        response.setNickname(user.getNickname());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRoles(user.getRolesStr());
        response.setIsLocked(user.getIsLocked());
        return ResponseEntity.ok(response);
    }

    /**
     * Scheduled job to delete user with expired token every 24 hour.
     */
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    @Transactional
    public void cleanupNonVerifiedUsers() {
        System.out.println("=== Cleaning up non-verified users : " + LocalDateTime.now());
        List<User> users = userRepository.findAllByExpiresToken(LocalDateTime.now());
        for (User user : users) {
            // remove national id image of seller from image storage
            if (user.getSellerProfile() != null &&
                user.getSellerProfile().getNationalIdImageFront() != null &&
                user.getSellerProfile().getNationalIdImageBack() != null)
            {
                imageStorageService.deleteImage(user.getSellerProfile().getNationalIdImageFront());
                imageStorageService.deleteImage(user.getSellerProfile().getNationalIdImageBack());
            }
            userRepository.delete(user);
        }
    }
}