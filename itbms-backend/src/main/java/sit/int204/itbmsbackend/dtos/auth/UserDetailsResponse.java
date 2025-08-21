package sit.int204.itbmsbackend.dtos.auth;

import lombok.Data;

@Data
public class UserDetailsResponse {
    private Integer id;
    private String nickname;
    private String fullName;
    private String email;
    private Boolean isLocked;
}
