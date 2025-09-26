package sit.int204.itbmsbackend.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderItemRequest {
    @NotNull
    public Integer saleItemId;
    @NotNull
    public Integer quantity;
}