package sit.int204.itbmsbackend.controllers.v2;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v2/moderator")
public class ModeratorController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> moderatorDashboard() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to moderator dashboard");
        response.put("access", "MODERATOR, ADMIN only");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/moderate-content")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> moderateContent(@RequestBody Map<String, String> contentData) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Content moderated successfully");
        response.put("action", "Content reviewed by moderator");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getReports() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Moderation reports");
        response.put("count", "5 pending reports");
        return ResponseEntity.ok(response);
    }
}
