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
import sit.int204.itbmsbackend.dtos.brand.BrandCreateUpdateDto;
import sit.int204.itbmsbackend.dtos.brand.BrandDetailDto;
import sit.int204.itbmsbackend.dtos.brand.BrandListDto;
import sit.int204.itbmsbackend.services.BrandService;

@RestController
@RequestMapping("/v1/brands")
public class BrandControllerV1 {
    private final BrandService brandService;

    @Autowired
    public BrandControllerV1(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<List<BrandListDto>> getAllBrand() {
        return ResponseEntity.ok().body(brandService.getAllBrand());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDetailDto> getBrandById(@PathVariable Integer id) {
        return ResponseEntity.ok(brandService.getBrandByIdDto(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandDetailDto> updateBrand(@PathVariable Integer id, @Valid @RequestBody BrandDetailDto brandDto) {
        brandDto.setId(id);
        BrandDetailDto updated = brandService.updateBrand(brandDto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping
    public ResponseEntity<BrandDetailDto> createBrand(@Valid @RequestBody BrandCreateUpdateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(brandService.createBrand(dto));
    }

    @DeleteMapping("/{id}") //Soft Delete
    public ResponseEntity<Void> deleteBrand(@PathVariable Integer id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
