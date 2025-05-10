package sit.int204.itbmsbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int204.itbmsbackend.dtos.MessageResponse;
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
    public ResponseEntity<List<ListSaleItemRes>> getAllSaleItems(
            @RequestParam(defaultValue = "") String brand,
            @RequestParam(defaultValue = "asc") String sort
    ) {
        return ResponseEntity.ok(saleItemService.findAll(brand, sort));
    }

    @GetMapping("/pages")
    public ResponseEntity<PageDTO<ListSaleItemRes>> getCustomerPages(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "5") Integer pageSize)
    {
        return ResponseEntity.ok(saleItemService.findAll(pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailSaleItemRes> getSaleItemById(@PathVariable Integer id) {
        return ResponseEntity.ok(saleItemService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CreateUpdateSaleItemRes> addSaleItem(@RequestBody CreateSaleItemReq saleItem) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleItemService.addSaleItem(saleItem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreateUpdateSaleItemRes> updateProduct(@PathVariable Integer id, @RequestBody UpdateSaleItemReq saleItem) {
        saleItem.setId(id);
        return ResponseEntity.ok(saleItemService.updateSaleItem(saleItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        saleItemService.removeSaleItem(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
