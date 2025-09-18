package sit.int204.itbmsbackend.dto.auth;

import lombok.Data;
import sit.int204.itbmsbackend.constant.UserType;

import java.util.Set;

@Data
public class UserDetailsResponse {
    private Integer id;
    private String nickname;
    private String fullName;
    private String email;
    private Set<String> roles;
    private Boolean isLocked;
    private UserType userType;
}
