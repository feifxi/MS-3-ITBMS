package sit.int204.itbmsbackend.dto.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdatedCartItemResponse {
    public Integer saleItemId;
    public Integer availableQuantity;
    public BigDecimal newPrice;
    public List<String> errorCase;
    public List<String> errorMessages;
}