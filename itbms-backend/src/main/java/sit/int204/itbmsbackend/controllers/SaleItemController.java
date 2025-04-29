package sit.int204.itbmsbackend.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int204.itbmsbackend.dtos.SaleItemDetailDto;
import sit.int204.itbmsbackend.dtos.SaleItemListDto;
import sit.int204.itbmsbackend.entities.SaleItem;
import sit.int204.itbmsbackend.services.SaleItemService;
import sit.int204.itbmsbackend.utils.ListMapper;

import java.util.List;

@RestController
@RequestMapping("/v1/sale-items")
@CrossOrigin(origins = "${app.cors.allowedOrigins}")
public class SaleItemController {
    private final SaleItemService saleItemService;
    private final ModelMapper modelMapper;
    private final ListMapper listMapper;

    @Autowired
    public SaleItemController(SaleItemService saleItemService, ModelMapper modelMapper, ListMapper listMapper) {
        this.saleItemService = saleItemService;
        this.modelMapper = modelMapper;
        this.listMapper = listMapper;
    }

    @GetMapping
    public ResponseEntity<List<SaleItemListDto>> getAllSaleItems() {
        List<SaleItem> customers = saleItemService.findAll();
        return ResponseEntity.ok().body(
                listMapper.mapList(customers, SaleItemListDto.class, modelMapper)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleItemDetailDto> getSaleItemById(@PathVariable Integer id) {
        SaleItem saleItem = saleItemService.findById(id);
        return ResponseEntity.ok(modelMapper.map(saleItem, SaleItemDetailDto.class));
    }

    @PostMapping
    public SaleItem addSaleItem(@RequestBody SaleItem saleItem) {
        return saleItemService.addSaleItem(saleItem);
    }

    @PutMapping("/{id}")
    public SaleItem updateProduct(@PathVariable Integer id, @RequestBody SaleItem saleItem) {
        saleItem.setId(id);
        return saleItemService.updateSaleItem(saleItem);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Integer id) {
        return saleItemService.removeSaleItem(id);
    }
}
