package sit.int204.itbmsbackend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.int204.itbmsbackend.entities.SaleItem;
import sit.int204.itbmsbackend.entities.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Integer>, JpaSpecificationExecutor<SaleItem> {
    Page<SaleItem> findByBrandNameIn(List<String> brands, Pageable pageable);
    List<SaleItem> findSaleItemByPriceBetween(BigDecimal priceAfter, BigDecimal priceBefore);
    List<SaleItem> findAllBySeller(User seller);
    Page<SaleItem> findAllBySellerId(Integer id, Pageable pageable);

    @Query("SELECT DISTINCT s.storageGb FROM SaleItem s ORDER BY s.storageGb ASC")
    List<Integer> findDistinctStorageGb();
}

