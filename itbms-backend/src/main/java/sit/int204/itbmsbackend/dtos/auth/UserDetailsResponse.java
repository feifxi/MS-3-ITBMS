package sit.int204.itbmsbackend.dtos.auth;

import lombok.Data;

import java.util.Set;

@Data
public class UserDetailsResponse {
    private Integer id;
    private String nickname;
    private String fullName;
    private String email;
    private Set<String> roles;
    private Boolean isLocked;
}
