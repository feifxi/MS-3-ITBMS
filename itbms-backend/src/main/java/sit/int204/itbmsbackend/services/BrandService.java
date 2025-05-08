package sit.int204.itbmsbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.entities.Brand;
import sit.int204.itbmsbackend.repositories.BrandRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BrandService {
    private final BrandRepository brandRepository;
    @Autowired
    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
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




}