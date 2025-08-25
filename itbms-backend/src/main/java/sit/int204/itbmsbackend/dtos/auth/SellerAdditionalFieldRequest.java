package sit.int204.itbmsbackend.dtos.auth;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SellerAdditionalFieldRequest {
    private String shopName;
    private String phone;
    private String bankAccountNumber;
    private String bankName;
    private String nationalId;
    private MultipartFile nationalIdImageFront;
    private MultipartFile nationalIdImageBack;
}
