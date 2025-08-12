package sit.int204.itbmsbackend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import sit.int204.itbmsbackend.entities.SaleItem;

import java.math.BigDecimal;
import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Integer>, JpaSpecificationExecutor<SaleItem> {
    Page<SaleItem> findByBrandNameIn(List<String> brands, Pageable pageable);
    List<SaleItem> findSaleItemByPriceBetween(BigDecimal priceAfter, BigDecimal priceBefore);


}