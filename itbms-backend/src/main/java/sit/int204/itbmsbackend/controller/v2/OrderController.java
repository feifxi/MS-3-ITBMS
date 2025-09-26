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
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(
                userPrincipal.getId(),
                ordersRequest
        ));
    }

    // Get a single order detail
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Integer id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        OrderResponse order = orderService.getOrderById(id);
        if (!order.getBuyerId().equals(userPrincipal.getId()) && !order.getSeller().getId().equals(userPrincipal.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(order);
    }
}
