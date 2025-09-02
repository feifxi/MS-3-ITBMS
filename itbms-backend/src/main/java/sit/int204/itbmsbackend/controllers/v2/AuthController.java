package sit.int204.itbmsbackend.controllers.v2;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.dtos.auth.*;
import sit.int204.itbmsbackend.dtos.common.ApiResponse;
import sit.int204.itbmsbackend.security.UserPrincipal;
import sit.int204.itbmsbackend.services.AuthService;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/v2/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60;

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
        AuthTokenResponse authToken = authService.authenticateUser(loginRequest);

        // Set refresh token as http-only cookie
        String refreshToken = authToken.getRefreshToken();
        ResponseCookie cookie = generateCookie("refreshToken", refreshToken, COOKIE_MAX_AGE);
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(authToken);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
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