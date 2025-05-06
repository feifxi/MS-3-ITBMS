package sit.int204.itbmsbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int204.itbmsbackend.repositories.BrandRepository;

@Service
public class BrandService {
    private final BrandRepository brandRepository;
    @Autowired
    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

}