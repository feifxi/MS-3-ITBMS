package sit.int204.itbmsbackend.controllers.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.dtos.common.PageDTO;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemListResponseDTO;
import sit.int204.itbmsbackend.entities.User;
import sit.int204.itbmsbackend.security.UserPrincipal;
import sit.int204.itbmsbackend.services.SaleItemService;

@RestController
@RequestMapping("/v2/sellers")
@RequiredArgsConstructor
public class SellerController {
    private final SaleItemService saleItemService;

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
}
