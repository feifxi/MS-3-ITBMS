package sit.int204.itbmsbackend.dtos.auth;

import lombok.Data;

import java.util.Set;

@Data
public class AuthTokenResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private Integer id;
    private String nickname;
    private String email;
    private Set<String> roles;

    public AuthTokenResponse(String accessToken, String refreshToken, Integer id, String nickname, String email, Set<String> roles) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.roles = roles;
    }
}
