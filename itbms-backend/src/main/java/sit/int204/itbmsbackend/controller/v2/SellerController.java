package sit.int204.itbmsbackend.controller.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.dto.common.PageDTO;
import sit.int204.itbmsbackend.dto.order.OrderResponse;
import sit.int204.itbmsbackend.dto.saleItem.SaleItemListResponseDTO;
import sit.int204.itbmsbackend.entity.User;
import sit.int204.itbmsbackend.security.UserPrincipal;
import sit.int204.itbmsbackend.service.OrderService;
import sit.int204.itbmsbackend.service.SaleItemService;
import sit.int204.itbmsbackend.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/v2/sellers")
@RequiredArgsConstructor
public class SellerController {
    private final SaleItemService saleItemService;
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping("/{id}/sale-items")
    public ResponseEntity<PageDTO<SaleItemListResponseDTO>> getAllSaleItems(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Integer id,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdOn") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        User loggedInUser = userPrincipal.getUser();
        if (!loggedInUser.getRolesStr().contains("ADMIN") && !loggedInUser.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(saleItemService.findAllBySellerId(id, page, size, sortField, sortDirection));
    }

    // Get all orders by seller
    @GetMapping("/{id}/orders")
    public ResponseEntity<PageDTO<OrderResponse>> getOrdersBySeller(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Integer id,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdOn") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        userService.findById(id);
        return ResponseEntity.ok(orderService.getOrdersBySeller(id,  page, size, sortField, sortDirection));
    }
}
