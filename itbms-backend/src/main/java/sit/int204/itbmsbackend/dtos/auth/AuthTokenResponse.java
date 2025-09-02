package sit.int204.itbmsbackend.dtos.auth;

import lombok.Data;

import java.util.Set;

@Data
public class AuthTokenResponse {
    private String accessToken;
    private String refreshToken;
    private String type = "Bearer";
    private Integer id;
    private String nickname;
    private String fullName;
    private String email;
    private String userType;
    private Set<String> roles;
    private Boolean isLocked;

    public AuthTokenResponse(String accessToken, String refreshToken, Integer id, String nickname,String fullName, String email, String userType,Set<String> roles, Boolean isLocked) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.nickname = nickname;
        this.fullName = fullName;
        this.email = email;
        this.userType = userType;
        this.roles = roles;
        this.isLocked = isLocked;
    }
}
