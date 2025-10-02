package sit.int204.itbmsbackend.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponse {
    public Integer saleItemId;
    public String saleItemName;
    public String saleItemImage;
    public BigDecimal priceAtPurchase;
    public Integer quantity;
}