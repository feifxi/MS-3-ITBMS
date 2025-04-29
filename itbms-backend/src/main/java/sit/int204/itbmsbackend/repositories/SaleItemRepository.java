package sit.int204.itbmsbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int204.itbmsbackend.entities.SaleItem;

public interface SaleItemRepository extends JpaRepository<SaleItem, Integer> {
}