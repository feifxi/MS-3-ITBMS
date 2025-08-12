package sit.int204.itbmsbackend.controllers.v1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemCreateDto;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemDetailDto;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemListDto;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemResponseDto;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemUpdateDto;
import sit.int204.itbmsbackend.services.SaleItemService;


@RestController
@RequestMapping("/v1/sale-items")
public class SaleItemControllerV1 {
    private final SaleItemService saleItemService;

    @Autowired
    public SaleItemControllerV1(SaleItemService saleItemService) {
        this.saleItemService = saleItemService;
    }

    @GetMapping
    public ResponseEntity<List<SaleItemListDto>> getAllSaleItems() {
        return ResponseEntity.ok(saleItemService.findAll());
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
