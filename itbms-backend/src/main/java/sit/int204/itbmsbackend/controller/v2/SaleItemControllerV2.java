package sit.int204.itbmsbackend.controller.v2;


import java.math.BigDecimal;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sit.int204.itbmsbackend.dto.common.PageDTO;
import sit.int204.itbmsbackend.dto.saleItem.SaleItemListResponseDTO;
import sit.int204.itbmsbackend.service.SaleItemService;


@RestController
@RequestMapping("/v2/sale-items")
@RequiredArgsConstructor
public class SaleItemControllerV2 {
    private final SaleItemService saleItemService;

    @GetMapping("/storage-sizes")
    public ResponseEntity<List<Integer>> getDistinctStorageSizes() {
        List<Integer> storages = saleItemService.getDistinctStorageSizes();
        return ResponseEntity.ok(storages);
    }

    @GetMapping
    public ResponseEntity<PageDTO<SaleItemListResponseDTO>> getAllSaleItems(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdOn") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) List<String> filterBrands,
            @RequestParam(required = false) Integer priceLower,
            @RequestParam(required = false) Integer priceUpper,
            @RequestParam(required = false) List<Integer> storageSizes,
            @RequestParam(required = false) String searchKeyword
    ) {
        return ResponseEntity.ok(
                saleItemService.findAll(
                        filterBrands,
                        page,
                        size,
                        sortField,
                        sortDirection,
                        priceLower != null ? BigDecimal.valueOf(priceLower) : null,
                        priceUpper != null ? BigDecimal.valueOf(priceUpper) : null,
                        storageSizes,
                        searchKeyword

                )
        );

    }
}

