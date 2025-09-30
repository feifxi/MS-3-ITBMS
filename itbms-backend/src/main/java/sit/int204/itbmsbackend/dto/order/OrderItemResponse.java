package sit.int204.itbmsbackend.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemResponse {
    public Integer saleItemId;
    public String saleItemName;
    public String saleItemImage;
    public Integer quantity;
}
