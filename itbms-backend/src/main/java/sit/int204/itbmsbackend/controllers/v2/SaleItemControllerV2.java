package sit.int204.itbmsbackend.controllers.v2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sit.int204.itbmsbackend.dtos.PageDTO;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemListDto;
import sit.int204.itbmsbackend.services.SaleItemService;


@RestController
@RequestMapping("/v2/sale-items")
public class SaleItemControllerV2 {
    private final SaleItemService saleItemService;

    @Autowired
    public SaleItemControllerV2(SaleItemService saleItemService) {
        this.saleItemService = saleItemService;
    }

    @GetMapping
    public ResponseEntity<PageDTO<SaleItemListDto>> getAllSaleItems(
            @RequestParam(required = false) List<String> filterBrands,
            @RequestParam(required = true) Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdOn") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "1") Integer priceLower,
            @RequestParam(defaultValue = "1") Integer priceUpper,
            @RequestParam(required = false) List<String> storageSize
            ) {
//        System.out.println(filterBrands == null ? "null" : filterBrands.toString());
        return ResponseEntity.ok(saleItemService.findAll(filterBrands, page, size, sortField, sortDirection));
    }
}
