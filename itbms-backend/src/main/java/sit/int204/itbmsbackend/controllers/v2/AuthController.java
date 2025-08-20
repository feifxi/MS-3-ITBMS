package sit.int204.itbmsbackend.controllers.v2;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.dtos.common.ApiResponse;
import sit.int204.itbmsbackend.dtos.auth.*;
import sit.int204.itbmsbackend.entities.*;
import sit.int204.itbmsbackend.jwt.JwtUtils;
import sit.int204.itbmsbackend.repositories.RoleRepository;
import sit.int204.itbmsbackend.repositories.UserRepository;
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
    @Value("${email.verification_token_expiration_hr:86400000}") // 24 hours default
    private int emailVerifiedExpirationHr;

    @PostMapping(
            value = "/buyer/registers",
            consumes = {"multipart/form-data"},
            produces = {"application/json"}
    )
    public ResponseEntity<?> registerBuyerUser(@Valid @ModelAttribute BuyerRegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Email is already in use!");
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
        Role userRole = roleRepository.findOneByName("ROLE_USER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error: Role is not found!"));
        roles.add(userRole);
        user.setRoles(roles);

        // Create new buyer profile of user
        BuyerProfile buyerProfile = new BuyerProfile();
        buyerProfile.setUser(user);
        user.setBuyerProfile(buyerProfile);

        // Save user to db
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Buyer user registered successfully!"));
    }

    @PostMapping(
            value = "/seller/registers",
            consumes = {"multipart/form-data"},
            produces = {"application/json"}
    )
    public ResponseEntity<?> registerSellerUser(@Valid @ModelAttribute SellerRegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Email is already in use!");
        }

        // Save National ID Images
        String nationalIdImageFrontFileName = null;
        String nationalIdImageBackFileName = null;
        if (registerRequest.getNationalIdImageFront() != null && !registerRequest.getNationalIdImageFront().isEmpty() &&
            registerRequest.getNationalIdImageBack() != null && !registerRequest.getNationalIdImageBack().isEmpty())
        {
            nationalIdImageFrontFileName = imageStorageService.saveImage(registerRequest.getNationalIdImageFront());
            nationalIdImageBackFileName = imageStorageService.saveImage(registerRequest.getNationalIdImageBack());
        }

        // Create new user's account
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
        Role userRole = roleRepository.findOneByName("ROLE_USER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error: Role is not found!"));
        roles.add(userRole);
        user.setRoles(roles);

        // Create new seller profile of user
        SellerProfile sellerProfile = new SellerProfile();
        sellerProfile.setUser(user);
        sellerProfile.setPhone(registerRequest.getPhone());
        sellerProfile.setShopName(registerRequest.getShopName());
        sellerProfile.setBankName(registerRequest.getBankName());
        sellerProfile.setBankAccountNumber(registerRequest.getBankAccountNumber());
        sellerProfile.setNationalId(registerRequest.getNationalId());
        // Save image name to db
        if (nationalIdImageFrontFileName != null && nationalIdImageBackFileName != null) {
            sellerProfile.setNationalIdImageFront(nationalIdImageFrontFileName);
            sellerProfile.setNationalIdImageBack(nationalIdImageBackFileName);
        }
        user.setSellerProfile(sellerProfile);

        // Save user to db
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Seller user registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);

        User userDetails = (User) authentication.getPrincipal();

//        // Create refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails);
        // Get authority
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(new AuthTokenResponse(
                jwt,
                refreshToken.getToken(),
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        ));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateJwtTokenFromEmail(user.getEmail());
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
                    return ResponseEntity.ok(new RefreshTokenResponse(token, newRefreshToken.getToken()));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Refresh token not found."));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is authenticated
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "No user is currently logged in."));
        }

        User userDetails = (User) authentication.getPrincipal();
        refreshTokenService.deleteByUser(userDetails);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new ApiResponse(true, "User signed out successfully!"));
    }

    @PostMapping("/email-verifications")
    public ResponseEntity<?> emailVerification(@Valid @RequestBody EmailVerificationRequest request) {
        User user = userRepository.findByVerificationTokenUnExpires(request.getToken(), LocalDateTime.now()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token or token expires."));
        user.setStatus("ACTIVE");
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse(true, "Email verification successfully!"));
    }

    /**
     * Scheduled job to delete expired verified token of user every 24 hour.
     */
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    @Transactional
    public void cleanupNonVerifiedUsers() {
        System.out.println("==== Cleaning up non-verified users : " + LocalDateTime.now() + "====");
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