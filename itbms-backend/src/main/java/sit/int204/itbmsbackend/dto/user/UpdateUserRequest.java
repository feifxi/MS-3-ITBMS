package sit.int204.itbmsbackend.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import sit.int204.itbmsbackend.constant.UserType;

@Data
public class UpdateUserRequest {
    @NotNull
    private UserType userType;  // "BUYER" or "SELLER"

    @Size(max = 40)
    @NotNull
    private String nickname;

    @Size(max = 40)
    @NotNull
    private String fullName;

    // Seller additional field
    private String shopName;
    private String phone;
    private String bankAccountNumber;
    private String bankName;
    private String idCardNumber;
    private MultipartFile idCardImageFront;
    private MultipartFile idCardImageBack;
    private Boolean keptIdCardImageFront;
    private Boolean keptIdCardImageBack;
}
