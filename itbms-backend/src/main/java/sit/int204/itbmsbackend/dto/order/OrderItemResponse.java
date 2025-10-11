package sit.int204.itbmsbackend.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponse {
    public Integer saleItemId;
    public String saleItemName;
    public String saleItemBrand;
    public String saleItemImage;
    public String saleItemColor;
    public Integer saleItemStorageSize;
    public String saleItemDescription;
    public Integer quantity;
    public BigDecimal priceAtPurchase;
    public BigDecimal subTotalPrice;
}