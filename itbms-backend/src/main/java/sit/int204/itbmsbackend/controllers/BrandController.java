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
import sit.int204.itbmsbackend.dtos.Brand.BrandDetailDto;
import sit.int204.itbmsbackend.dtos.Brand.BrandDto;



import java.util.List;

@RestController
@RequestMapping("/v1/brands")
public class BrandController {
    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService, ModelMapper modelMapper, ListMapper listMapper, BrandRepository brandRepository) {
        this.brandService = brandService;
    }
    @GetMapping
    public ResponseEntity<List<BrandDto>> getAllBrandDtos() {
        return ResponseEntity.ok().body(brandService.getAllBrandDtos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDetailDto> getBrandByIdDto(@PathVariable Integer id) {
        return ResponseEntity.ok(brandService.getBrandByIdDto(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandDetailDto> updateBrand(@PathVariable Integer id, @Valid @RequestBody BrandDetailDto brandDto) {
        brandDto.setId(id); // ใช้ id จาก URL เสมอ
        BrandDetailDto updated = brandService.updateBrand(brandDto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping
    public ResponseEntity<BrandDetailDto> createBrand(@Valid @RequestBody CreateBrandDto dto) {
        return ResponseEntity.ok(brandService.createBrand(dto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Integer id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }


}
