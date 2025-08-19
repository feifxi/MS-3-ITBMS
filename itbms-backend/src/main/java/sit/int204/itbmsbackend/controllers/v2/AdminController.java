package sit.int204.itbmsbackend.controllers.v2;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v2/admin")
public class AdminController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminDashboard() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to admin dashboard");
        response.put("access", "ADMIN only");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "List of all users");
        response.put("totalUsers", 150);
        response.put("activeUsers", 120);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userId}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> banUser(@PathVariable Integer userId) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "User banned successfully");
        response.put("userId", userId.toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/system/maintenance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> systemMaintenance(@RequestBody Map<String, String> maintenanceData) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "System maintenance initiated");
        response.put("status", "Maintenance mode activated");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        response.put("userId", userId.toString());
        return ResponseEntity.ok(response);
    }
}
