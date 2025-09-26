package sit.int204.itbmsbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.constant.OrderStatus;
import sit.int204.itbmsbackend.constant.PaymentMethod;
import sit.int204.itbmsbackend.constant.PaymentStatus;
import sit.int204.itbmsbackend.dto.order.CreateOrderItemRequest;
import sit.int204.itbmsbackend.dto.order.CreateOrderRequest;
import sit.int204.itbmsbackend.entity.*;
import sit.int204.itbmsbackend.repository.*;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final SaleItemRepository saleItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public Set<Order> createOrder(Integer buyerId, List<CreateOrderRequest> orderRequests) {
        Set<Order> resultOrders = new LinkedHashSet<>();

        for (CreateOrderRequest orderRequest : orderRequests) {
            BigDecimal orderTotalAmount = BigDecimal.ZERO;
            Set<OrderItem> orderItems = new LinkedHashSet<>();
            Order order = new Order();

            for (CreateOrderItemRequest item :orderRequest.getOrderItems()) {
                SaleItem saleItem = saleItemRepository.findById(item.getSaleItemId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Sale item not found"));

//                if (saleItem.getQuantity() < item.getQuantity()) {
//                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, saleItem.getModel() + " quantity is less than the requested quantity :: " + item.getQuantity());
//                }
                BigDecimal price = saleItem.getPrice();
                BigDecimal subtotal = price.multiply(new BigDecimal(item.getQuantity()));
                orderTotalAmount = orderTotalAmount.add(subtotal);

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setSaleItem(saleItem);
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPriceAtPurchase(price);

                orderItems.add(orderItem);

                // Decrease stock sale item
//                saleItem.setQuantity(saleItem.getQuantity() - item.getQuantity());
//                saleItemRepository.save(saleItem);
            }

            User buyer = userRepository.findById(buyerId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Buyer not found"));
            User seller = userRepository.findById(orderRequest.getSellerId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Seller not found"));
            Address buyerAddress = buyer.getAddresses().stream().findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Buyer address not found"));
            // Set order
            order.setOrderItems(orderItems);
            order.setTotalAmount(orderTotalAmount);
            order.setBuyer(buyer);
            order.setSeller(seller);
            order.setAddress(buyerAddress);
            order.setStatus(OrderStatus.PENDING);
            order.setPaymentStatus(PaymentStatus.PENDING);
            // Set payment
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setAmount(orderTotalAmount);
            payment.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
            payment.setStatus(PaymentStatus.PENDING);
            payment.setTransactionReference(UUID.randomUUID().toString());

            order.setPayment(payment);

            order = orderRepository.save(order);
            resultOrders.add(order);
        }

        return resultOrders;
    }

    public List<Order> getOrdersByBuyer(Integer buyerId) {
        return orderRepository.findByBuyer_Id(buyerId);
    }

    public Order getOrderById(Integer orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")
        );
    }
}
