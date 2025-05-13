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
        List<Brand> brands = brandRepository.findAllByOrderByNameAsc();
        return listMapper.mapList(brands, BrandDto.class, modelMapper);
    }

    public BrandDetailDto getBrandByIdDto(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found"));
        return modelMapper.map(brand, BrandDetailDto.class);
    }


    public BrandDetailDto updateBrand(BrandDetailDto brandDto) {
        Brand existing = brandRepository.findById(brandDto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand Item not found"));

        existing.setName(brandDto.getName());
        existing.setWebsiteUrl(brandDto.getWebsiteUrl());
        existing.setIsActive(brandDto.getIsActive());
        existing.setCountryOfOrigin(brandDto.getCountryOfOrigin());
        existing.setUpdatedOn(LocalDateTime.now());

        Brand saved = brandRepository.save(existing);
        return modelMapper.map(saved, BrandDetailDto.class);
    }

    public BrandDetailDto createBrand(CreateUpdateBrandDto dto) {
        String brandName = dto.getName();
        String trimmedName = brandName.trim();

        if (!brandName.isEmpty() && Character.isWhitespace(brandName.charAt(0))) {
            throw new IllegalArgumentException("Brand name must not start with a space");
        }

        List<Brand> existingBrands = brandRepository.findByNameIgnoreCase(trimmedName);
        boolean duplicateExists = existingBrands.stream()
                .anyMatch(b -> b.getName().trim().equalsIgnoreCase(trimmedName));

        if (duplicateExists) {
            throw new IllegalArgumentException("This brand name already exists in the system.");
        }

        Brand brand = modelMapper.map(dto, Brand.class);
        brand.setName(trimmedName);
        brand.setIsActive(true);

        // ไม่ต้องตั้งค่า createdOn และ updatedOn ในที่นี้ เพราะฐานข้อมูลจะจัดการให้
        brand = brandRepository.save(brand);
        return modelMapper.map(brand, BrandDetailDto.class);
    }
    @Transactional
    public void deleteBrand(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cannot delete: No brand found with ID = " + id));

        if (Boolean.FALSE.equals(brand.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brand is already inactive.");
        }

        // Soft delete the brand
        brand.setIsActive(false);
        // ไม่ต้องเซต updatedOn — DB จัดการเองแล้ว
        brandRepository.save(brand);
    }

    @Transactional
    public BrandDetailDto restoreBrand(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cannot restore: No brand found with ID = " + id));

        if (Boolean.TRUE.equals(brand.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brand is already active.");
        }

        brand.setIsActive(true);
        Brand saved = brandRepository.save(brand);

        return modelMapper.map(saved, BrandDetailDto.class);
    }







}