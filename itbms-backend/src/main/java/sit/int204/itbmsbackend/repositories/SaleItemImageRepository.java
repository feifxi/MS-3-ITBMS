package sit.int204.itbmsbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.int204.itbmsbackend.entities.SaleItem;
import sit.int204.itbmsbackend.entities.SaleItemImage;

import java.util.List;

@Repository
public interface SaleItemImageRepository extends JpaRepository<SaleItemImage, Integer> {
    List<SaleItemImage> findAllBySaleItem(SaleItem saleItem);
}