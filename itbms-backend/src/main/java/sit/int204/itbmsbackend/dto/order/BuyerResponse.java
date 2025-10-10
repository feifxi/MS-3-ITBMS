package sit.int204.itbmsbackend.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyerResponse {
    private Integer id;
    private String email;
    private String nickname;
    private String fullName;
}