package sit.int204.itbmsbackend.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int204.itbmsbackend.dtos.brand.CreateBrandDto;
import sit.int204.itbmsbackend.entities.Brand;
import sit.int204.itbmsbackend.repositories.BrandRepository;
import sit.int204.itbmsbackend.services.BrandService;
import sit.int204.itbmsbackend.utils.ListMapper;
import sit.int204.itbmsbackend.dtos.brand.BrandDetailDto;
import sit.int204.itbmsbackend.dtos.brand.BrandDto;



import java.util.List;

@RestController
@RequestMapping("/v1/brands")
public class BrandController {
    private final BrandService brandService;
    private final ModelMapper modelMapper;
    private final ListMapper listMapper;
    private final BrandRepository brandRepository;

    @Autowired
    public BrandController(BrandService brandService, ModelMapper modelMapper, ListMapper listMapper, BrandRepository brandRepository) {
        this.brandService = brandService;
        this.modelMapper = modelMapper;
        this.listMapper = listMapper;
        this.brandRepository = brandRepository;
    }
    @GetMapping
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        List<Brand> brands = brandService.getAllBrands();
        return ResponseEntity.ok().body(
                listMapper.mapList(brands, BrandDto.class, modelMapper)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDetailDto> getBrandById(@PathVariable Integer id) {
        Brand brand = brandService.getBrandById(id);
        return ResponseEntity.ok(modelMapper.map(brand, BrandDetailDto.class));
    }
    @PutMapping("/{id}")
    public Brand updateBrand(@PathVariable Integer id, @RequestBody Brand brand) {
        brand.setId(id);
        return brandService.updateBrand(brand);
    }
    @PostMapping
    public BrandDetailDto createBrand(@Valid @RequestBody CreateBrandDto dto) {
        return brandService.createBrand(dto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Integer id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }


}
