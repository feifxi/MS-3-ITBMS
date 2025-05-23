package sit.int204.itbmsbackend.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int204.itbmsbackend.dtos.PageDTO;
import sit.int204.itbmsbackend.dtos.saleItem.*;
import sit.int204.itbmsbackend.services.SaleItemService;

import java.util.List;


@RestController
@RequestMapping("/v1/sale-items")
public class SaleItemController {
    private final SaleItemService saleItemService;

    @Autowired
    public SaleItemController(SaleItemService saleItemService) {
        this.saleItemService = saleItemService;}

    @GetMapping
    public ResponseEntity<List<SaleItemListDto>> getAllSaleItems(
            @RequestParam(defaultValue = "") String brand,
            @RequestParam(defaultValue = "asc") String sort
    ) {
        return ResponseEntity.ok(saleItemService.findAll(brand, sort));
    }

    @GetMapping("/pages")
    public ResponseEntity<PageDTO<SaleItemListDto>> getCustomerPages(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "5") Integer pageSize)
    {
        return ResponseEntity.ok(saleItemService.findAll(pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleItemDetailDto> getSaleItemById(@PathVariable Integer id) {
        return ResponseEntity.ok(saleItemService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SaleItemResponseDto> addSaleItem(@Valid @RequestBody SaleItemCreateDto saleItem) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleItemService.addSaleItem(saleItem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleItemResponseDto> updateProduct(@Valid @PathVariable Integer id, @RequestBody SaleItemUpdateDto saleItem) {
        saleItem.setId(id);
        return ResponseEntity.ok(saleItemService.updateSaleItem(saleItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        saleItemService.removeSaleItem(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
