package sit.int204.itbmsbackend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.dtos.Brand.BrandDetailDto;
import sit.int204.itbmsbackend.dtos.Brand.CreateBrandDto;
import sit.int204.itbmsbackend.entities.Brand;
import sit.int204.itbmsbackend.repositories.BrandRepository;
import sit.int204.itbmsbackend.repositories.SaleItemRepository;
import sit.int204.itbmsbackend.utils.ListMapper;

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
    public List<Brand> getAllBrands(){return brandRepository.findAll();}
    public  Brand getBrandById(Integer id)
    {return brandRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Brand Item not found")
    );}
    public Brand updateBrand(Brand brand) {
        Brand existing = brandRepository.findById(brand.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand Item not found"));

        existing.setName(brand.getName());
        existing.setWebsiteUrl(brand.getWebsiteUrl());
        existing.setIsActive(brand.getIsActive());
        existing.setCountryOfOrigin(brand.getCountryOfOrigin());
        existing.setUpdatedOn(LocalDateTime.now());

        return brandRepository.save(existing);
    }

    public BrandDetailDto createBrand(CreateBrandDto dto) {
        String brandName = dto.getName();

        // ตัดช่องว่างหน้า-หลัง
        String trimmedName = brandName.trim();

        // ตรวจสอบว่าชื่อเริ่มด้วยช่องว่าง
        if (!brandName.isEmpty() && Character.isWhitespace(brandName.charAt(0))) {
            throw new IllegalArgumentException("ชื่อแบรนด์ห้ามขึ้นต้นด้วยช่องว่าง");
        }

        // ตรวจสอบชื่อซ้ำ โดยไม่สนใจตัวพิมพ์เล็ก/ใหญ่ และเว้นวรรค
        List<Brand> existingBrands = brandRepository.findByNameIgnoreCase(trimmedName);
        boolean duplicateExists = existingBrands.stream()
                .anyMatch(b -> b.getName().trim().equalsIgnoreCase(trimmedName));

        if (duplicateExists) {
            throw new IllegalArgumentException("ชื่อแบรนด์นี้มีอยู่แล้วในระบบ");
        }


        // สร้าง brand
        Brand brand = modelMapper.map(dto, Brand.class);
        brand.setName(trimmedName); // ใช้ชื่อที่ตัดเว้นวรรคแล้ว
        brand.setIsActive(true);
        brand.setCreatedOn(ZonedDateTime.now(ZoneId.of("Asia/Bangkok")));
        brand.setUpdatedOn(LocalDateTime.now(ZoneId.of("Asia/Bangkok")));

        brand = brandRepository.save(brand);

        return modelMapper.map(brand, BrandDetailDto.class);
    }






}