package sit.int204.itbmsbackend.controller.v2;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.dto.order.CreateOrderRequest;
import sit.int204.itbmsbackend.dto.order.OrderItemResponse;
import sit.int204.itbmsbackend.dto.order.OrderResponse;
import sit.int204.itbmsbackend.dto.order.SellerResponse;
import sit.int204.itbmsbackend.entity.Order;
import sit.int204.itbmsbackend.security.UserPrincipal;
import sit.int204.itbmsbackend.service.OrderService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/v2/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // Create a new order
    @PostMapping
    public ResponseEntity<List<OrderResponse>> createOrder(@Valid @RequestBody List<CreateOrderRequest> ordersRequest, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Set<Order> orders = orderService.createOrder(
                userPrincipal.getId(),
                ordersRequest
        );

        List<OrderResponse> orderListResponse = orders.stream().map(OrderController::mappedToDTO).toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(orderListResponse);
    }

    // Get a single order detail
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Integer id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Order order = orderService.getOrderById(id);
        if (!order.getBuyer().getId().equals(userPrincipal.getId()) && !order.getSeller().getId().equals(userPrincipal.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(mappedToDTO(order));
    }

    public static OrderResponse mappedToDTO(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(order.getId());
        orderResponse.setOrderDate(order.getCreatedOn());
        orderResponse.setBuyerId(order.getBuyer().getId());
        orderResponse.setOrderNote("Noob Note");
        orderResponse.setOrderStatus(order.getStatus());
        orderResponse.setTotalAmount(order.getTotalAmount());
        orderResponse.setShippingAddress(order.getAddress().getAddressLine() + " " +  order.getAddress().getCity());
        // Order Item
        List<OrderItemResponse> orderItemRes = order.getOrderItems().stream().map((item) -> {
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setSaleItemId(item.getSaleItem().getId());
            itemResponse.setSaleItemName(item.getSaleItem().getModel());
            itemResponse.setQuantity(item.getQuantity());
            return itemResponse;
        }).toList();
        orderResponse.setOrderItems(orderItemRes);
        // Seller
        SellerResponse sellerResponse = new SellerResponse();
        sellerResponse.setFullName(order.getSeller().getFullName());
        sellerResponse.setNickname(order.getSeller().getNickname());
        sellerResponse.setEmail(order.getSeller().getEmail());
        sellerResponse.setPhone(order.getSeller().getPhone());
        sellerResponse.setId(order.getSeller().getId());

        orderResponse.setSeller(sellerResponse);

        return orderResponse;
    }
}
