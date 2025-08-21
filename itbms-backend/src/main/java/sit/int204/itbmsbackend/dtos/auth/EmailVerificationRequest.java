package sit.int204.itbmsbackend.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailVerificationRequest {
    @NotBlank
    private String token;
}
