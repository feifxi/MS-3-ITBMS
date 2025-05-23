package sit.int204.itbmsbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int204.itbmsbackend.entities.Brand;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    Brand findByNameEqualsIgnoreCase(String name);
    Boolean existsByNameIgnoreCase(String name);
}