package sit.int204.itbmsbackend.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
public class AuthTokenResponse {
    private String token;
    private String type = "Bearer";
    private Integer id;
    private String username;
    private String email;
    private Set<String> roles;

    public AuthTokenResponse(String accessToken, Integer id, String username, String email, Set<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
