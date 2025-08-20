package sit.int204.itbmsbackend.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BuyerRegisterRequest {
    @Size(max = 40)
    @NotNull
    private String nickname;

    @Size(max = 40)
    @NotNull
    private String fullName;

    @Size(max = 100)
    @NotNull
    private String email;

    @Size(min = 8)
    @NotNull
    private String password;
}
