package sit.int204.itbmsbackend.dto.user;

import lombok.Data;
import sit.int204.itbmsbackend.constant.UserStatus;
import sit.int204.itbmsbackend.constant.UserType;
import sit.int204.itbmsbackend.entity.Address;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class UserProfileResponse {
    private Integer id;
    private String nickname;
    private String fullName;
    private String email;
    private String shopName;
    private String phone;
    private String bankAccountNumber;
    private String bankName;
    private String idCardNumber;
    private String idCardImageFront;
    private String idCardImageBack;
    private Boolean isLocked = false;
    private UserType userType;
    private UserStatus status;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private Set<Address> addresses = new LinkedHashSet<>();
    private Set<String> roles = new LinkedHashSet<>();
}
