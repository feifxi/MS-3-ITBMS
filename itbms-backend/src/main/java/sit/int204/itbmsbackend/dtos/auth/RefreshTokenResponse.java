package sit.int204.itbmsbackend.dtos.auth;

import lombok.Data;

@Data
public class RefreshTokenResponse {
    private String refreshToken;
    private String accessToken;
    private String tokenType = "Bearer";

    public RefreshTokenResponse(String refreshToken, String accessToken) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }
}
