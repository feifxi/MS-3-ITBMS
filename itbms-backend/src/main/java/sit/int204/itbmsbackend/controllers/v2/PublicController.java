package sit.int204.itbmsbackend.controllers.v2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// ===== Public Controller (No Authentication Required) =====
@RestController
@RequestMapping("/v2/public")
public class PublicController {

    @GetMapping("/hello")
    public ResponseEntity<?> publicEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello! This is a public endpoint - no authentication required");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    public ResponseEntity<?> publicInfo() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is public information");
        response.put("version", "1.0");
        return ResponseEntity.ok(response);
    }
}