//package sit.int204.itbmsbackend.controllers.v2;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import sit.int204.itbmsbackend.services.SaleItemService;
//
//import java.util.List;
//
//
//@RestController
//@RequestMapping("/v2/sale-items")
//public class SaleItemController {
//    private final SaleItemService saleItemService;
//
//    @Autowired
//    public SaleItemController(SaleItemService saleItemService) {
//        this.saleItemService = saleItemService;
//    }
//
//    @GetMapping
////    public ResponseEntity<PageDTO<SaleItemListDto>> getAllSaleItems(
//    public Object getAllSaleItems(
//            @RequestParam(required = false) List<String> filterBrands,
//            @RequestParam(defaultValue = "1") Integer page,
//            @RequestParam(defaultValue = "5") Integer size,
//            @RequestParam(defaultValue = "") String sortField,
//            @RequestParam(defaultValue = "asc") String sortDirection
//    ) {
//        return filterBrands;
////        return ResponseEntity.ok(saleItemService.findAll(page, size));
//    }
//}
