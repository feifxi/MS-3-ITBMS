package sit.int204.itbmsbackend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.entities.Brand;
import sit.int204.itbmsbackend.repositories.BrandRepository;
import sit.int204.itbmsbackend.utils.ListMapper;
import sit.int204.itbmsbackend.dtos.brand.BrandDetailDto;
import sit.int204.itbmsbackend.dtos.brand.BrandListDto;
import sit.int204.itbmsbackend.dtos.brand.BrandCreateUpdateDto;

import java.util.List;

@Service
public class BrandService {
    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper ;
    private final ListMapper listMapper;

    @Autowired
    public BrandService(BrandRepository brandRepository, ModelMapper modelMapper, ListMapper listMapper) {
        this.brandRepository = brandRepository;
        this.modelMapper = modelMapper;
        this.listMapper = listMapper;
    }

    public List<BrandListDto> getAllBrand() {
        List<Brand> brands = brandRepository.findAll();
        return listMapper.mapList(brands, BrandListDto.class, modelMapper);
    }

    public  Brand getBrandById(Integer id) {
        return brandRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Brand Item not found")
        );
    }

    public BrandDetailDto getBrandByIdDto(Integer id) {
        Brand brand = brandRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found")
        );
        return modelMapper.map(brand, BrandDetailDto.class);
    }

    public BrandDetailDto createBrand(BrandCreateUpdateDto brandDto) {
        if (brandRepository.existsByNameIgnoreCase(brandDto.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This brand name already exists");
        }
        brandDto.setName(brandDto.getName());
        brandDto.setIsActive(brandDto.getIsActive() != null ? brandDto.getIsActive() : false);
        Brand newBrand = brandRepository.save(
                modelMapper.map(brandDto, Brand.class)
        );
        return modelMapper.map(newBrand, BrandDetailDto.class);
    }

    public BrandDetailDto updateBrand(BrandDetailDto brandDto) {
        Brand existing = brandRepository.findById(brandDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand Item not found")
        );
        Brand duplicateBrandName = brandRepository.findByNameEqualsIgnoreCase(brandDto.getName());
        if (duplicateBrandName != null && !existing.getId().equals(duplicateBrandName.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This brand name already exists");
        }
        existing.setName(brandDto.getName().trim());
        existing.setWebsiteUrl(brandDto.getWebsiteUrl());
        existing.setIsActive(brandDto.getIsActive() != null ? brandDto.getIsActive() : false);
        existing.setCountryOfOrigin(brandDto.getCountryOfOrigin());
        return modelMapper.map(brandRepository.save(existing), BrandDetailDto.class);
    }

    public void deleteBrand(Integer id) {
        Brand brand = brandRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot delete: Brand with ID = " + id + " not found."
        ));
        if (!brand.getSaleItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete: Brand has related sale items.");
        }
        brandRepository.delete(brand);
    }

}