package sit.int204.itbmsbackend.dtos.auth;

import lombok.Data;

@Data
public class RefreshTokenResponse {
    private String accessToken;
    private String tokenType = "Bearer";

    public RefreshTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
