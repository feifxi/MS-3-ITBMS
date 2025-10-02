package sit.int204.itbmsbackend.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateOrderItemRequest {
    @NotNull
    public Integer saleItemId;
    @NotNull
    public String model;
    @NotNull
    public Integer quantity;
    @NotNull
    public BigDecimal price;
}