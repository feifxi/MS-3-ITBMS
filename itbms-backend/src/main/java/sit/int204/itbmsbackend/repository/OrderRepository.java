package sit.int204.itbmsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.int204.itbmsbackend.entity.Brand;
import sit.int204.itbmsbackend.entity.Order;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByBuyer_IdOrderByCreatedOnDesc(Integer buyerId);
}