package sit.int204.itbmsbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import sit.int204.itbmsbackend.services.BrandService;

@RestController
public class BrandController {
    private final BrandService brandService;
    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }


}
