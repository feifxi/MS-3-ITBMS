package sit.int204.itbmsbackend.controllers.v2;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
    @Autowired
    private Validator validator;

    @Value("${team.code:ms3}")
    private String teamCode;
    @Value("${email.verification_token_expiration_hr:86400000}") // 24 hours default
    private int emailVerifiedExpirationHr;
    private final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60;
    private final String COOKIE_REFRESH_PATH = "/itb-mshop/v2/auth/refresh";
    private final boolean COOKIE_HTTPS = false;

    @PostMapping(
            value = "/registers/buyer",
            consumes = {"multipart/form-data"},
            produces = {"application/json"}
    )
    public ResponseEntity<?> registerBuyerUser(@Valid @ModelAttribute BuyerRegisterRequest registerRequest) {
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
//        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(emailVerifiedExpirationHr));
        user.setVerificationTokenExpiry(LocalDateTime.now().plusMinutes(1));

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

        // Send email verification
        emailService.sendVerificationEmail(user.getEmail(), verifiedToken, teamCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Buyer user registered successfully!"));
    }

    @PostMapping(
            value = "/registers/seller",
            consumes = {"multipart/form-data"},
            produces = {"application/json"}
    )
    public ResponseEntity<?> registerSellerUser(@Valid @ModelAttribute SellerRegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use!");
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

        // Send email verification
        emailService.sendVerificationEmail(user.getEmail(), verifiedToken, teamCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Seller user registered successfully!"));
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
        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        // Generate token
        String accessToken = jwtUtils.generateJwtToken(user);
        RefreshToken refreshTokenData = refreshTokenService.createRefreshToken(user);

        // Set refresh token as cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshTokenData.getToken())
                .httpOnly(true)      // Not accessible by JS
                .secure(COOKIE_HTTPS)        // Only send over HTTPS (set false for dev HTTP)
                .path(COOKIE_REFRESH_PATH) // Only send to refresh endpoint
                .maxAge(COOKIE_MAX_AGE) // 7 days in seconds
                .sameSite("Strict")  // Prevent CSRF (can also use "Lax")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(new AuthTokenResponse(
                accessToken,
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                roles
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
                    ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken.getToken())
                            .httpOnly(true)
                            .secure(COOKIE_HTTPS)
                            .path(COOKIE_REFRESH_PATH)
                            .maxAge(COOKIE_MAX_AGE)
                            .sameSite("Strict")
                            .build();
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

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        refreshTokenService.deleteByUser(userPrincipal.getUser());
        SecurityContextHolder.clearContext();

        ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(COOKIE_HTTPS)
                .path(COOKIE_REFRESH_PATH)
                .maxAge(0) // deletes cookie
                .build();
        response.addHeader("Set-Cookie", clearCookie.toString());

        return ResponseEntity.ok(new ApiResponse(true, "User signed out successfully!"));
    }

    @PostMapping("/email-verifications")
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

    @GetMapping("/check-identity")
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