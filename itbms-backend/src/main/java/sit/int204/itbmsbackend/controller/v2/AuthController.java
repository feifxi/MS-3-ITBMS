package sit.int204.itbmsbackend.controller.v2;

import io.micrometer.core.instrument.Timer;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.dto.auth.*;
import sit.int204.itbmsbackend.dto.common.ApiResponse;
import sit.int204.itbmsbackend.entity.UserTracking;
import sit.int204.itbmsbackend.repository.UserTrackingRepository;
import sit.int204.itbmsbackend.security.UserPrincipal;
import sit.int204.itbmsbackend.service.AuthService;
import sit.int204.itbmsbackend.service.MetricsService;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/v2/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final Environment environment;
    private final MetricsService metricsService;
    private final UserTrackingRepository userTracking;
    private final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // 7 days in seconds

    private ResponseCookie generateCookie(String name, String value, int maxAge) {
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isDevMode = activeProfiles.length > 0 && activeProfiles[0].equals("dev");
        return ResponseCookie.from(name, value)
                .httpOnly(true)      // Not accessible by JS
                .secure(false)        // Only send over HTTPS (set false for dev HTTP)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Lax")
                .build();
    }

    @PostMapping(
            value = "/registers",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse> registerUser(@Valid @ModelAttribute RegisterRequest request) throws MessagingException, UnsupportedEncodingException {
        authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "User registered successfully!"));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse> emailVerification(@Valid @RequestBody EmailVerificationRequest request) {
        authService.emailVerification(request.getToken());
        return ResponseEntity.ok(new ApiResponse(true, "Email verification successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Timer.Sample sample = metricsService.startTimer();
        try {
            // Login Logic
            AuthTokenResponse authToken = authService.authenticateUser(loginRequest);

            // Set refresh token as http-only cookie
            String refreshToken = authToken.getRefreshToken();
            ResponseCookie cookie = generateCookie("refreshToken", refreshToken, COOKIE_MAX_AGE);
            response.addHeader("Set-Cookie", cookie.toString());

            // Metric logging for monitoring
            metricsService.incrementUserLogin();
            // Tracking User Activity
            userTracking.save(new UserTracking(
                    authToken.getId().toString(),
                    authToken.getFullName(),
                    "Logged in",
                    LocalDateTime.now().toString()
            ));
            return ResponseEntity.ok(authToken);
        } finally {
            metricsService.stopTimer(sample);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@CookieValue(name = "refreshToken") String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No refresh token provided");
        }
        RefreshTokenResponse refreshTokenResponse =  authService.refreshToken(refreshToken);

        // Set refresh token as http-only cookie
        ResponseCookie cookie = generateCookie("refreshToken", refreshTokenResponse.getRefreshToken(), COOKIE_MAX_AGE);
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(refreshTokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logoutUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            HttpServletResponse response
    ) {
        if (userPrincipal == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "No user is currently logged in."));
        }

        authService.logoutUser(userPrincipal.getUser());
        SecurityContextHolder.clearContext();

        // Clear refresh token cookie
        ResponseCookie clearCookie = generateCookie("refreshToken", "", 0);
        response.addHeader("Set-Cookie", clearCookie.toString());

        return ResponseEntity.ok(new ApiResponse(true, "User signed out successfully!"));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetailsResponse> checkUserIdentity(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in.");
        }
        UserDetailsResponse response = authService.checkUserIdentity(userPrincipal.getId());
        return ResponseEntity.ok(response);
    }
}