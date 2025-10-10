package sit.int204.itbmsbackend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.int204.itbmsbackend.constant.OrderStatus;
import sit.int204.itbmsbackend.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findByBuyer_Id(Integer buyerId, Pageable pageable);
    Page<Order> findByBuyer_IdAndStatus(Integer buyer_Id, OrderStatus orderStatus, Pageable pageable);
    Page<Order> findBySeller_Id(Integer sellerId, Pageable pageable);
    Page<Order> findBySeller_IdAndStatus(Integer sellerId, OrderStatus orderStatus, Pageable pageable);
}