package sit.int204.itbmsbackend.dto.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdatedCartItemResponse {
    public Integer saleItemId;
    public Integer availableQuantity;
    public BigDecimal newPrice;
    public String message;
}
