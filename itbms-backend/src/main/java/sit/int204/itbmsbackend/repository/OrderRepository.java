package sit.int204.itbmsbackend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.int204.itbmsbackend.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findByBuyer_Id(Integer buyerId, Pageable pageable);
    Page<Order> findBySeller_Id(Integer sellerId, Pageable pageable);
}