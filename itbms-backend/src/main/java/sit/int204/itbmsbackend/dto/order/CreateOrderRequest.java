package sit.int204.itbmsbackend.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {
    @NotNull
    private Integer sellerId;
    @NotNull
    private Integer buyerId;
    @NotNull
    private List<CreateOrderItemRequest> orderItems;
    @NotNull
    private String orderNote;
    @NotNull
    private String shippingAddress;
    @NotNull
    private LocalDateTime orderDate;
}
