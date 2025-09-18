package sit.int204.itbmsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.int204.itbmsbackend.entity.SaleItem;
import sit.int204.itbmsbackend.entity.SaleItemImage;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaleItemImageRepository extends JpaRepository<SaleItemImage, Integer> {
    Optional<SaleItemImage> findOneByImageName(String imageName);
    List<SaleItemImage> findAllBySaleItemOrderByImageViewOrder(SaleItem saleItem);
}