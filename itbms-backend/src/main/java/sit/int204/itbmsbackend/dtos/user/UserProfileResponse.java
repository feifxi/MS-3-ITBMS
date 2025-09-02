package sit.int204.itbmsbackend.dtos.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import sit.int204.itbmsbackend.entities.Address;
import sit.int204.itbmsbackend.entities.Role;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    private String userType;
    private String status;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private Set<Address> addresses = new LinkedHashSet<>();
    private Set<String> roles = new LinkedHashSet<>();
}
