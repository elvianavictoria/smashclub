package com.backendsyndicate.smashclub.ecommerce.service;

import com.backendsyndicate.smashclub.auth.repository.UserRepository;
import com.backendsyndicate.smashclub.common.constant.CartStatusConstant;
import com.backendsyndicate.smashclub.common.constant.OrderStatusConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionTypeConstant;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.ecommerce.core.IOrder;
import com.backendsyndicate.smashclub.ecommerce.dto.request.ReqBuyNowDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.response.*;
import com.backendsyndicate.smashclub.ecommerce.model.*;
import com.backendsyndicate.smashclub.ecommerce.repo.CartRepo;
import com.backendsyndicate.smashclub.ecommerce.repo.OrderItemRepo;
import com.backendsyndicate.smashclub.ecommerce.repo.OrderRepo;
import com.backendsyndicate.smashclub.ecommerce.repo.ProductVariantRepo;
import com.backendsyndicate.smashclub.payment.service.PaymentService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService implements IOrder {
    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private OrderItemRepo orderItemRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductVariantRepo productVariantRepo;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepo;

    //    @Autowired
    private ModelMapper modelMapper = new ModelMapper();

    private String generateErrorCode(String methodNo, String errorNo) {
        return "ORD-" + methodNo + "E" + errorNo;
    }

    @Override
    public RespCreateOrderDTO createOrder(String userId) {
        RespCartDTO cartDTO = cartService.getOrCreateActiveCart(userId);
        try{
        Cart cart = modelMapper.map(cartDTO, Cart.class);

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        for (CartItem item : cart.getCartItems()) {
            Optional<ProductVariant> availVariant = productVariantRepo.findByIdAndSufficientStock(item.getVariant().getId(), item.getQuantity());
            if (availVariant.isEmpty()) {
                Logging.handleException("OrderService", "buyNow", 74, generateErrorCode("01", "001"), "Product variant not found");
                return null;
            } else {
                ProductVariant productVariant = availVariant.get();
                productVariant.setStock(productVariant.getStock() - item.getQuantity());
            }
        }

        Order order = new Order();
        order.setUser(userRepo.findById(userId).get());
        order.setStatus(OrderStatusConstant.ORDER_PAYMENT_PENDING);
        order.setOrderDate(LocalDateTime.now());
        orderRepo.save(order);

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getCartItems()) {

            BigDecimal totalPrice =
                    cartItem.getVariant().getPrice()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setVariant(cartItem.getVariant());
            orderItem.setPriceAtPurchase(cartItem.getVariant().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            order.setTotalPrice(totalPrice);

            orderItemRepo.save(orderItem);

            subtotal = subtotal.add(totalPrice);
        }

        order.setSubTotal(subtotal);
        order.setTotalPrice(subtotal);
        orderRepo.save(order);

        paymentService.createTransaction(
                userId,
                order.getTotalPrice(),
                order.getId().toString(),
                TransactionTypeConstant.ECOMMERCE_SHOPPING
        );

        cart.setStatus((byte) CartStatusConstant.ORDER_CHECKED_OUT);
        cartRepo.save(cart);

        RespCreateOrderDTO response = new RespCreateOrderDTO();
        response.setOrderId(order.getId());
        response.setStatus(order.getStatus());
        response.setTotalPrice(order.getTotalPrice());
        return response;}
        catch (Exception ex){
            Logging.handleException("OrderService", "createOrder", 128, generateErrorCode("01", "010"), ex.getMessage());
            return null;
        }
    }

    /**
     * Code: 02
     *
     * @param userId
     * @param request
     * @return
     */
    @Override
    public RespCreateOrderDTO buyNow(String userId, ReqBuyNowDTO request) {
        Optional<ProductVariant> availVariant = productVariantRepo.findByIdAndSufficientStock(request.getVariantId(), request.getQuantity());
        try {if (availVariant.isEmpty()) {
            Logging.handleException("OrderService", "buyNow", 144, generateErrorCode("02", "001"), "Product variant not found");
            return null;
        }

        ProductVariant productVariant = availVariant.get();
        productVariant.setStock(productVariant.getStock() - request.getQuantity());

        Order order = new Order();
        order.setUser(userRepo.findById(userId).get());
        order.setStatus(OrderStatusConstant.ORDER_PAYMENT_PENDING);
        order.setOrderDate(LocalDateTime.now());
        orderRepo.save(order);

        BigDecimal price = productVariant.getPrice();
        BigDecimal total = price.multiply(BigDecimal.valueOf(request.getQuantity()));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setVariant(productVariant);
        orderItem.setPriceAtPurchase(price);
        orderItem.setQuantity(request.getQuantity());
        order.setTotalPrice(total);
        orderItemRepo.save(orderItem);

        order.setSubTotal(total);
        order.setTotalPrice(total);
        orderRepo.save(order);

        paymentService.createTransaction(
                userId,
                total,
                order.getId().toString(),
                TransactionTypeConstant.ECOMMERCE_SHOPPING
        );

        RespCreateOrderDTO response = new RespCreateOrderDTO();
        response.setOrderId(order.getId());
        response.setStatus(order.getStatus());
        response.setTotalPrice(order.getTotalPrice());
        return response;}
        catch (Exception e) {
            Logging.handleException("OrderService", "buyNow(String userId, ReqBuyNowDTO request)", 185, generateErrorCode("03", "010"), e.getMessage());
            return null;
        }
    }

    /**
     * Code: 03
     *
     * @param orderId
     * @param newStatus
     */
    @Override
    public void updateOrderStatus(Long orderId, byte newStatus) {
        Optional<Order> optOrder = orderRepo.findById(orderId);
        try{
        if (optOrder.isEmpty()) {
            Logging.handleException("OrderService", "updateOrderStatus(Long orderId, byte newStatus)", 199, generateErrorCode("04", "001"), "Order not found");
        }

        Order order = optOrder.get();
        byte currentStatus = order.getStatus();

        if (!OrderStatusConstant.isValidTransition(currentStatus, newStatus)) {
            Logging.handleException("OrderService", "updateOrderStatus(Long orderId, byte newStatus)", 207, generateErrorCode("04", "002"), "Invalid transition");
        }

        order.setStatus(newStatus);
        orderRepo.save(order);}
        catch (Exception ex){
            Logging.handleException("OrderService", "updateOrderStatus(Long orderId, byte newStatus)", 214, generateErrorCode("04", "010"), ex.getMessage());
        }
    }

    /**
     * Code: 04
     *
     * @param orderId
     */
    @Override
    public void cancelOrder(Long orderId) {
        Optional<Order> optOrder = orderRepo.findById(orderId);
        try{
        if (optOrder.isEmpty()) {
            Logging.handleException("OrderService", "updateOrderStatus", 212, generateErrorCode("03", "001"), "Order not found");
        }

        Order order = optOrder.get();
        order.setStatus((byte) OrderStatusConstant.ORDER_CANCELLED);

        List<OrderItem> orderItems = order.getOrderItem();
        for (OrderItem orderItem : orderItems) {
            ProductVariant productVariant = productVariantRepo.findById(orderItem.getVariant().getId()).get();
            productVariant.setStock(productVariant.getStock() + orderItem.getQuantity());
        }

        orderRepo.save(order);}
        catch (Exception ex){
            Logging.handleException("OrderService", "cancelOrder(Long orderId)", 242, generateErrorCode("04", "010"), ex.getMessage());
        }
    }

    /**
     * Code: 05
     *
     * @param orderId
     * @param userId
     * @return
     */

    @Override
    @Transactional
    public RespOrderDetailDTO getOrderDetail(Long orderId, String userId) {
        Order order = orderRepo.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        try{
        List<RespOrderItemDTO> items = order.getOrderItem()
                .stream()
                .map(item -> RespOrderItemDTO.builder()
                        .variantId(item.getVariant().getId())
                        .quantity(item.getQuantity())
                        .price(item.getPriceAtPurchase())
                        .totalPrice(item.getTotalPrice())
                        .build()
                )
                .toList();

        return RespOrderDetailDTO.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .totalPrice(order.getTotalPrice())
                .items(items)
                .build();}
        catch (Exception ex){
            Logging.handleException("OrderService", "getOrderDetail(Long orderId, String userId)", 279, generateErrorCode("05", "010"), ex.getMessage());
            return null;
        }
    }

    /**
     * Code: 06
     *
     * @param userId
     * @param page
     * @param size
     * @return
     */

    @Override
    @Transactional
    public Page<RespOrderSummaryDTO> getUserOrderHistory(String userId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        try{
        Page<Order> orders = orderRepo.findByUserIdOrderByOrderDateDesc(userId, pageable);

        return orders.map(order ->
                RespOrderSummaryDTO.builder()
                        .orderId(order.getId())
                        .status(order.getStatus())
                        .orderDate(order.getOrderDate())
                        .totalPrice(order.getTotalPrice())
                        .build()
        );} catch (Exception e) {
            Logging.handleException("OrderService", "getUserOrderHistory(String userId, int page, int size)", 309, generateErrorCode("06", "010"), e.getMessage());
            return null;
        }
    }
}

