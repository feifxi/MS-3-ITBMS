package sit.int204.itbmsbackend.controllers.v2;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sit.int204.itbmsbackend.services.SaleItemService;


@RestController
@RequestMapping("/v2/sale-items")
public class SaleItemControllerV2 {
    private final SaleItemService saleItemService;

    @Autowired
    public SaleItemControllerV2(SaleItemService saleItemService) {
        this.saleItemService = saleItemService;
    }

    @GetMapping("/storage-sizes")
    public ResponseEntity<List<Integer>> getDistinctStorageSizes() {
        List<Integer> storages = saleItemService.getDistinctStorageSizes();
        return ResponseEntity.ok(storages);
    }

    @GetMapping
    public ResponseEntity<?> getAllSaleItems(
            @RequestParam(required = false) List<String> filterBrands,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdOn") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) Integer priceLower,
            @RequestParam(required = false) Integer priceUpper,
            @RequestParam(required = false) List<Integer> storageSizes,
            @RequestParam(required = false, defaultValue = "false") boolean distinctStorage
    ) {
        if (distinctStorage) {
            List<Integer> storages = saleItemService.getDistinctStorageSizes();
            return ResponseEntity.ok(storages);
        }

        return ResponseEntity.ok(
                saleItemService.findAll(
                        filterBrands,
                        page,
                        size,
                        sortField,
                        sortDirection,
                        priceLower != null ? BigDecimal.valueOf(priceLower) : null,
                        priceUpper != null ? BigDecimal.valueOf(priceUpper) : null,
                        storageSizes
                )
        );

    }
}

/*    @GetMapping
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
    }*/

