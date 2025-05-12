package sit.int204.itbmsbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int204.itbmsbackend.entities.Brand;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    List<Brand> findAllByOrderByNameAsc();
    List<Brand> findByIsActiveTrue(); // ถ้าใช้ในหน้า List เฉพาะที่ยัง active
    // ใน BrandRepository
    List<Brand> findByNameIgnoreCase(String name);
}