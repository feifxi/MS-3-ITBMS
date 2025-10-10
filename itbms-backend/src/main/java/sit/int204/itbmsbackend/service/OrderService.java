package sit.int204.itbmsbackend.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.constant.OrderStatus;
import sit.int204.itbmsbackend.constant.PaymentMethod;
import sit.int204.itbmsbackend.constant.PaymentStatus;
import sit.int204.itbmsbackend.dto.common.PageDTO;
import sit.int204.itbmsbackend.dto.order.*;
import sit.int204.itbmsbackend.entity.*;
import sit.int204.itbmsbackend.repository.*;
import sit.int204.itbmsbackend.util.ListMapper;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final SaleItemRepository saleItemRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ListMapper listMapper;

    public OrderValidationResponse validateOrders(Integer buyerId, List<CreateOrderRequest> orderRequests) {
        OrderValidationResponse orderValidateRes = new OrderValidationResponse();
        List<UpdatedCartItemResponse>  updatedCartItems = new ArrayList<>();
        boolean isValid = true;

        // Validate each item if there are any update data
        for (CreateOrderRequest orderReq : orderRequests) {
            for (CreateOrderItemRequest reqItem : orderReq.getOrderItems()) {
                SaleItem existSaleItem = saleItemRepository.findById(reqItem.getSaleItemId()).orElse(null);
                UpdatedCartItemResponse updateItem = new UpdatedCartItemResponse();
                List<String> errorCases =  new ArrayList<>();
                List<String> errorMessages =  new ArrayList<>();

                if (existSaleItem == null) {
                    errorCases.add("DELETED");
                    errorMessages.add("The "+ reqItem.getModel() +" has been deleted");
                    isValid = false;
                }
                else {
                    if (existSaleItem.getQuantity() == 0) {
                        errorCases.add("OUT_OF_QUANTITY");
                        errorMessages.add(reqItem.getModel() + " is out of stock.");
                        isValid = false;
                    }
                    else if (existSaleItem.getQuantity() - reqItem.getQuantity() < 0) {
                        errorCases.add("NOT_ENOUGH_QUANTITY");
                        errorMessages.add("Only " + existSaleItem.getQuantity() + " left in stock for " + reqItem.getModel() + ".");
                        isValid = false;
                    }
                    if (existSaleItem.getPrice().compareTo(reqItem.getPrice()) != 0) {
                        errorCases.add("PRICE_INCORRECT");
                        errorMessages.add("The price of " + reqItem.getModel() + " has been updated from ฿"+ reqItem.getPrice()  +" to ฿" + existSaleItem.getPrice());
                        isValid = false;
                    }
                    updateItem.setSaleItemId(existSaleItem.getId());
                    updateItem.setAvailableQuantity(existSaleItem.getQuantity());
                    updateItem.setNewPrice(existSaleItem.getPrice());
                }
                updateItem.setErrorCase(errorCases);
                updateItem.setErrorMessages(errorMessages);

                if (!updateItem.getErrorCase().isEmpty() && !updateItem.getErrorMessages().isEmpty()) {
                    updatedCartItems.add(updateItem);
                }
            }
        }

        orderValidateRes.setValid(isValid);
        orderValidateRes.setUpdateItems(updatedCartItems);
        return orderValidateRes;
    }

    @Transactional
    public List<OrderResponse> createOrder(Integer buyerId, List<CreateOrderRequest> orderRequests) {
        List<OrderResponse> resultOrders = new LinkedList<>();

        for (CreateOrderRequest orderRequest : orderRequests) {
            BigDecimal orderTotalAmount = BigDecimal.ZERO;
            Set<OrderItem> orderItems = new LinkedHashSet<>();
            Order order = new Order();
            OrderStatus orderStatus = OrderStatus.PENDING;

            for (CreateOrderItemRequest item : orderRequest.getOrderItems()) {
                SaleItem saleItem = saleItemRepository.findById(item.getSaleItemId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Sale item not found"));

                if (saleItem.getQuantity() < item.getQuantity()) {
                    orderStatus = OrderStatus.CANCELLED;
//                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, saleItem.getModel() + " quantity is less than the requested quantity :: " + item.getQuantity());
                }
                else if (saleItem.getPrice().compareTo(item.getPrice()) != 0) {
                    orderStatus = OrderStatus.CANCELLED;
//                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The price of " + saleItem.getModel() + " has been updated from ฿"+ item.getPrice()  +" to ฿" + saleItem.getPrice());
                }

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
                Integer currentQuantity = saleItem.getQuantity() - item.getQuantity();
                saleItem.setQuantity(currentQuantity);
                saleItemRepository.save(saleItem);
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
            order.setShippingAddressNote(orderRequest.getShippingAddress());
            order.setOrderNote(orderRequest.getOrderNote());
            order.setStatus(orderStatus);
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
            resultOrders.add(mappedToDTO(order));
        }

        return resultOrders;
    }

    public PageDTO<OrderResponse> getOrdersByBuyer(
            Integer buyerId,
            Integer page,
            Integer size,
            String sortField,
            String sortDirection,
            OrderStatus status
    ) {
        Sort sort = "desc".equalsIgnoreCase(sortDirection)
                ? Sort.by(sortField).descending().and(Sort.by("id").ascending())
                : Sort.by(sortField).ascending().and(Sort.by("id").ascending());
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orders;
        if (status != null) {
            orders = orderRepository.findByBuyer_IdAndStatus(buyerId, status, pageable);
        }
        else {
            orders = orderRepository.findByBuyer_Id(buyerId, pageable);
        }
        List<OrderResponse> content = orders.getContent().stream().map((this::mappedToDTO)).toList();
        PageDTO<OrderResponse> res = listMapper.toPageDTO(orders, OrderResponse.class, modelMapper);
        res.setContent(content);
        return res;
    }

    public PageDTO<OrderResponse> getOrdersBySeller(
            Integer sellerId,
            Integer page,
            Integer size,
            String sortField,
            String sortDirection,
            OrderStatus status
    ) {
        Sort sort = "desc".equalsIgnoreCase(sortDirection)
                ? Sort.by(sortField).descending().and(Sort.by("id").ascending())
                : Sort.by(sortField).ascending().and(Sort.by("id").ascending());
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orders;
        if (status != null) {
            orders = orderRepository.findBySeller_IdAndStatus(sellerId, status, pageable);
        }
        else {
            orders = orderRepository.findBySeller_Id(sellerId, pageable);
        }
        List<OrderResponse> content = orders.getContent().stream().map((this::mappedToDTO)).toList();
        PageDTO<OrderResponse> res = listMapper.toPageDTO(orders, OrderResponse.class, modelMapper);
        res.setContent(content);
        return res;
    }

    public OrderResponse getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")
        );
        return mappedToDTO(order);
    }

    public OrderResponse mappedToDTO(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(order.getId());
        orderResponse.setOrderDate(order.getCreatedOn());
        orderResponse.setBuyerId(order.getBuyer().getId());
        orderResponse.setOrderNote(order.getOrderNote());
        orderResponse.setOrderStatus(order.getStatus());
        orderResponse.setTotalAmount(order.getTotalAmount());
//        orderResponse.setShippingAddress(order.getAddress().getAddressLine() + " " +  order.getAddress().getCity());
        orderResponse.setShippingAddress(order.getShippingAddressNote());
        // Order Item
        List<OrderItemResponse> orderItemRes = order.getOrderItems().stream().map((item) -> {
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setSaleItemId(item.getSaleItem().getId());
            itemResponse.setSaleItemName(item.getSaleItem().getModel());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setPriceAtPurchase(item.getPriceAtPurchase());
            SaleItemImage firstImage = item.getSaleItem().getSaleItemImages().stream().findFirst().orElse(null);
            itemResponse.setSaleItemImage(firstImage.getImageName());
            return itemResponse;
        }).toList();
        orderResponse.setOrderItems(orderItemRes);
        // Seller
        SellerResponse sellerResponse = new SellerResponse();
        sellerResponse.setId(order.getSeller().getId());
        sellerResponse.setFullName(order.getSeller().getFullName());
        sellerResponse.setNickname(order.getSeller().getNickname());
        sellerResponse.setEmail(order.getSeller().getEmail());
        sellerResponse.setPhone(order.getSeller().getPhone());
        sellerResponse.setShopName(order.getSeller().getShopName());
        orderResponse.setSeller(sellerResponse);
        // Buyer
        BuyerResponse buyerResponse = new BuyerResponse();
        buyerResponse.setId(order.getBuyer().getId());
        buyerResponse.setFullName(order.getBuyer().getFullName());
        buyerResponse.setNickname(order.getBuyer().getNickname());
        buyerResponse.setEmail(order.getBuyer().getEmail());
        orderResponse.setBuyer(buyerResponse);

        return orderResponse;
    }
}
