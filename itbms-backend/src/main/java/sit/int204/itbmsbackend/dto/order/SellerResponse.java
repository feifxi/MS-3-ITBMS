package sit.int204.itbmsbackend.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerResponse {
    private Integer id;
    private String email;
    private String nickname;
    private String fullName;
    private String phone;
    private String shopName;
}
