package sit.int204.itbmsbackend.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class RegisterRequest {
    @NotBlank
    private String role;  // "BUYER" or "SELLER"

    // dynamic fields depending on role
    private Map<String, Object> data;
}
