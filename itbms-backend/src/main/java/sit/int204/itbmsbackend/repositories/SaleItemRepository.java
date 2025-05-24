package sit.int204.itbmsbackend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sit.int204.itbmsbackend.entities.SaleItem;

import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Integer> {
    Page<SaleItem> findByBrandNameIn(List<String> brands, Pageable pageable);
}