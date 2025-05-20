package sit.int204.itbmsbackend.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.entities.Brand;
import sit.int204.itbmsbackend.repositories.BrandRepository;
import sit.int204.itbmsbackend.repositories.SaleItemRepository;
import sit.int204.itbmsbackend.utils.ListMapper;
import sit.int204.itbmsbackend.dtos.brand.BrandDetailDto;
import sit.int204.itbmsbackend.dtos.brand.BrandDto;
import sit.int204.itbmsbackend.dtos.brand.CreateUpdateBrandDto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class BrandService {
    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper ;
    private final ListMapper listMapper;
    private final SaleItemRepository saleItemRepository;
    @Autowired
    public BrandService(BrandRepository brandRepository, ModelMapper modelMapper, ListMapper listMapper, SaleItemRepository saleItemRepository) {
        this.brandRepository = brandRepository;
        this.modelMapper = modelMapper;
        this.listMapper = listMapper;
        this.saleItemRepository = saleItemRepository;
    }
    //public List<Brand> getAllBrands(){return brandRepository.findAll();}
    public  Brand getBrandById(Integer id)
    {return brandRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Brand Item not found")
    );}

    public List<BrandDto> getAllBrandDtos() {
        List<Brand> brands = brandRepository.findAll();
        return listMapper.mapList(brands, BrandDto.class, modelMapper);
    }

    public BrandDetailDto getBrandByIdDto(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found"));
        return modelMapper.map(brand, BrandDetailDto.class);
    }

    public BrandDetailDto createBrand(CreateUpdateBrandDto brandDto) {
        if (brandRepository.existsByNameIgnoreCase(brandDto.getName())) {
            System.out.println(brandDto.getName());
            System.out.println(brandRepository.existsByNameIgnoreCase(brandDto.getName()));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This brand name already exists");
        }
        brandDto.setName(brandDto.getName().trim());
        brandDto.setIsActive(brandDto.getIsActive() != null && brandDto.getIsActive());

        Brand newBrand = brandRepository.save(
                modelMapper.map(brandDto, Brand.class)
        );
        return modelMapper.map(newBrand, BrandDetailDto.class);
    }

    public BrandDetailDto updateBrand(BrandDetailDto brandDto) {
        Brand existing = brandRepository.findById(brandDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand Item not found")
        );
        if (brandRepository.existsByNameIgnoreCase(brandDto.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This brand name already exists");
        }
        existing.setName(brandDto.getName().trim());
        existing.setWebsiteUrl(brandDto.getWebsiteUrl());
        existing.setIsActive(brandDto.getIsActive() != null && brandDto.getIsActive());
        existing.setCountryOfOrigin(brandDto.getCountryOfOrigin());
        existing.setUpdatedOn(LocalDateTime.now());
        return modelMapper.map(brandRepository.save(existing), BrandDetailDto.class);
    }

    @Transactional
    public void deleteBrand(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cannot delete: Brand with ID = " + id + " not found."
                ));
        if (brand.getSaleItems().size() > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot delete: Brand has related sale items."
            );
        }
        // ลบ brand จริงๆ (hard delete)
        brandRepository.delete(brand);
    }

}