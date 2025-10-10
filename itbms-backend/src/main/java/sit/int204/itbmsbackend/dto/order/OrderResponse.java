package sit.int204.itbmsbackend.dto.order;

import lombok.Getter;
import lombok.Setter;
import sit.int204.itbmsbackend.constant.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private Integer orderId;
    private SellerResponse seller;
    private Integer buyerId;
    private BuyerResponse buyer;
    private List<OrderItemResponse> orderItems;
    private String orderNote;
    private String shippingAddress;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
}