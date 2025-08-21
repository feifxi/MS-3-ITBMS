package sit.int204.itbmsbackend.dtos.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SellerRegisterRequest {
    @Size(max = 40)
    @NotNull
    private String nickname;

    @Size(max = 40)
    @NotNull
    private String fullName;

    @Size(max = 100)
    @NotNull
    private String email;

    @Size(min = 8)
    @NotNull
    private String password;

    @NotNull
    private String shopName;

    @Size(max = 20)
    @NotNull
    private String phone;

    @Size(max = 50)
    @NotNull
    private String bankAccountNumber;

    @Size(max = 50)
    @NotNull
    private String bankName;

    @Size(max = 20)
    private String nationalId;

    private MultipartFile nationalIdImageFront;
    private MultipartFile nationalIdImageBack;
}
