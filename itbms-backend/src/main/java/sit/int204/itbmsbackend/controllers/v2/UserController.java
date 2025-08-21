package sit.int204.itbmsbackend.controllers.v2;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v2/user")
public class UserController {

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> userProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User profile data");
        response.put("email", email);
        response.put("authorities", authentication.getAuthorities());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-profile/{userId}")
    @PreAuthorize("hasRole('USER') and #userId == principal.id")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> updateData, @PathVariable Integer userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Profile updated successfully for: " + username);
        return ResponseEntity.ok(response);
    }
}
