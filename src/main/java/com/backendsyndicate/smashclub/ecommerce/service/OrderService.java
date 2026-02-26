package com.backendsyndicate.smashclub.ecommerce.service;

import com.backendsyndicate.smashclub.auth.repository.UserRepository;
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
import com.backendsyndicate.smashclub.payment.dto.response.RespCreateTransactionDTO;
import com.backendsyndicate.smashclub.payment.model.RefundRequest;
import com.backendsyndicate.smashclub.payment.model.Transaction;
import com.backendsyndicate.smashclub.payment.service.PaymentService;
import com.backendsyndicate.smashclub.payment.service.TransactionService;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class OrderService implements IOrder {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");
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
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepo;

    private String generateErrorCode(String methodNo, String errorNo) {
        return "ORD-" + methodNo + "E" + errorNo;
    }

    @Override
    public RespCreateOrderDTO createOrder(String userId) {
        Cart cart = cartService.getActiveCartEntity(userId);
        try{
        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(userRepo.findById(userId).get());
        order.setStatus(OrderStatusConstant.ORDER_PAYMENT_PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderCode(generateOrderCode());

        BigDecimal subtotal = BigDecimal.ZERO;

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            Optional<ProductVariant> availVariant = productVariantRepo.findByIdAndSufficientStock(cartItem.getVariant().getId(), cartItem.getQuantity());
            if (availVariant.isEmpty()) {
                Logging.handleException("OrderService", "buyNow", 86, generateErrorCode("01", "001"), "Product variant not found");
                return null;
            }

            BigDecimal totalPrice = cartItem.getVariant().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductName(cartItem.getVariant().getProduct().getProductName());
            orderItem.setVariant(cartItem.getVariant());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getVariant().getPrice());
            orderItem.setTotalPrice(totalPrice);

            orderItems.add(orderItem);
            subtotal = subtotal.add(totalPrice);
        }

        order.setOrderItem(orderItems);
        order.setSubTotal(subtotal);
        order.setTotalPrice(subtotal);

            RespCreateTransactionDTO transactionDTO = paymentService.createTransaction(
                    order.getUser().getId(),
                    order.getTotalPrice(),
                    order.getOrderCode(),
                    TransactionTypeConstant.ECOMMERCE_SHOPPING
            );
            if (transactionDTO == null){
                Logging.handleException("OrderService", "buyNow(String userId, ReqBuyNowDTO request)", 117, generateErrorCode("02", "002"), "Transaction failed");
                return null;
            }
            Transaction transaction = transactionService.getTransaction(transactionDTO.getTransactionCode());
            Logging.printConsole(transaction.toString());
            order.setTransactionId(transaction);

        orderRepo.save(order);

        for (CartItem item : cart.getCartItems()) {
                ProductVariant productVariant = item.getVariant();
                productVariant.setStock(productVariant.getStock() - item.getQuantity());
                productVariantRepo.save(productVariant);
        }
        cartService.clearCart(userId);

        RespCreateOrderDTO response = new RespCreateOrderDTO();
        response.setOrderId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setOrderCode(order.getOrderCode());
        response.setTransactionId(order.getTransactionId().getId());
        response.setStatus(order.getStatus());
        response.setSubTotal(order.getSubTotal());
        response.setTotalPrice(order.getTotalPrice());
        response.setOrderDate(order.getOrderDate());
        return response;
        }
        catch (Exception ex){
            Logging.handleException("OrderService", "createOrder", 145, generateErrorCode("01", "010"), ex.getMessage());
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
            Logging.handleException("OrderService", "buyNow(String userId, ReqBuyNowDTO request)", 161, generateErrorCode("02", "001"), "Product variant not found");
            return null;
        }

        ProductVariant productVariant = availVariant.get();

        Order order = new Order();
        order.setUser(userRepo.findById(userId).get());
        order.setStatus(OrderStatusConstant.ORDER_PAYMENT_PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderCode(generateOrderCode());

        BigDecimal price = productVariant.getPrice();
        BigDecimal total = price.multiply(BigDecimal.valueOf(request.getQuantity()));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProductName(productVariant.getProduct().getProductName());
        orderItem.setVariant(productVariant);
        orderItem.setPriceAtPurchase(price);
        orderItem.setTotalPrice(total);
        orderItem.setQuantity(request.getQuantity());

        order.setSubTotal(total);
        order.setTotalPrice(total);

        RespCreateTransactionDTO transactionDTO = paymentService.createTransaction(
                order.getUser().getId(),
                order.getTotalPrice(),
                order.getOrderCode(),
                TransactionTypeConstant.ECOMMERCE_SHOPPING
            );
        if (transactionDTO == null){
            Logging.handleException("OrderService", "buyNow(String userId, ReqBuyNowDTO request)", 196, generateErrorCode("02", "002"), "Transaction failed");
            return null;
        }

        Transaction transaction = transactionService.getTransaction(transactionDTO.getTransactionCode());

        order.setTransactionId(transaction);
        order = orderRepo.save(order);
        orderItemRepo.save(orderItem);

        productVariant.setStock(productVariant.getStock() - request.getQuantity());
        productVariantRepo.save(productVariant);

        RespCreateOrderDTO response = new RespCreateOrderDTO();
        response.setUserId(order.getUser().getId());
        response.setOrderId(order.getId());
        response.setOrderCode(order.getOrderCode());
        response.setOrderDate(order.getOrderDate());
        response.setTransactionId(order.getTransactionId().getId());
        response.setPaymentLink(order.getTransactionId().getPaymentLink());
        response.setStatus(order.getStatus());
        response.setSubTotal(order.getSubTotal());
        response.setTotalPrice(order.getTotalPrice());
        return response;
        }
        catch (Exception e) {
            Logging.handleException("OrderService", "buyNow(String userId, ReqBuyNowDTO request)", 220, generateErrorCode("02", "010"), e.getMessage());
            return null;
        }
    }

    /**
     * Code: 03
     *
     * @param orderCode
     * @param newStatus
     */
    @Override
    public void updateOrderStatus(String orderCode, byte newStatus) {
        Optional<Order> optOrder = orderRepo.findByOrderCode(orderCode);
        try{
        if (optOrder.isEmpty()) {
            Logging.handleException("OrderService", "updateOrderStatus(Long orderId, byte newStatus)", 237, generateErrorCode("03", "001"), "Order not found");
        }

        Order order = optOrder.get();
        byte currentStatus = order.getStatus();

        if (!OrderStatusConstant.isValidTransition(currentStatus, newStatus)) {
            Logging.handleException("OrderService", "updateOrderStatus(Long orderId, byte newStatus)", 244, generateErrorCode("03", "002"), "Invalid transition");
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepo.save(order);}
        catch (Exception ex){
            Logging.handleException("OrderService", "updateOrderStatus(Long orderId, byte newStatus)", 251, generateErrorCode("04", "010"), ex.getMessage());
        }
    }

    /**
     * Code: 04
     *
     * @param orderCode
     */
    @Override
    public void cancelOrder(String orderCode) {
        Optional<Order> optOrder = orderRepo.findByOrderCode(orderCode);
        try{
        if (optOrder.isEmpty()) {
            Logging.handleException("OrderService", "cancelOrder(Long orderId)", 265, generateErrorCode("04", "001"), "Order not found");
        }

        Order order = optOrder.get();
        order.setStatus((byte) OrderStatusConstant.ORDER_CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = order.getOrderItem();
        for (OrderItem orderItem : orderItems) {
            ProductVariant productVariant = productVariantRepo.findById(orderItem.getVariant().getId()).get();
            productVariant.setStock(productVariant.getStock() + orderItem.getQuantity());
            productVariantRepo.save(productVariant);
        }

        orderRepo.save(order);}
        catch (Exception ex){
            Logging.handleException("OrderService", "cancelOrder(Long orderId)", 281, generateErrorCode("04", "010"), ex.getMessage());
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
        try {
            List<RespOrderItemDTO> items = order.getOrderItem()
                    .stream()
                    .map(item -> {
                                Hibernate.initialize(item.getVariant());
                                return RespOrderItemDTO.builder()
                                        .variantId(item.getVariant().getId())
                                        .variantName(item.getVariant().getVariantName())
                                        .productName(item.getVariant().getProduct().getProductName())
                                        .orderItemImgLink(item.getVariant().getVariantImgLink())
                                        .quantity(item.getQuantity())
                                        .price(item.getPriceAtPurchase())
                                        .totalPrice(item.getTotalPrice())
                                        .build();
                            }
                    )
                    .toList();

            RespOrderDetailDTO orderDetail = new RespOrderDetailDTO();
            orderDetail.setOrderId(order.getId());
            orderDetail.setOrderCode(order.getOrderCode());
            orderDetail.setStatus(order.getStatus());
            orderDetail.setOrderDate(order.getOrderDate());
            orderDetail.setUpdatedAt(order.getUpdatedAt());
            orderDetail.setSubtotal(order.getSubTotal());
            orderDetail.setTotalPrice(order.getTotalPrice());
            orderDetail.setItems(items);
            Logging.printConsole(orderDetail.getSubtotal().toString());

            RefundRequest refund = transactionService.getRefundRequestFromTransaction(order.getOrderCode());
            if (refund != null) {
                orderDetail.setRefundStatus(refund.getRefundStatus());
                orderDetail.setRefundRequestDate(refund.getCreatedAt());
                orderDetail.setRefundStatusUpdateDate(refund.getUpdatedAt());
            }

            String paymentLink = paymentService.getPaymentUrl(order.getOrderCode());
            if (paymentLink != null) {
                orderDetail.setPaymentLink(paymentLink);
            }

            return orderDetail;
        }
        catch (Exception ex){
            Logging.handleException("OrderService", "getOrderDetail(Long orderId, String userId)", 337, generateErrorCode("05", "010"), ex.getMessage());
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
                        .orderCode(order.getOrderCode())
                        .status(order.getStatus())
                        .orderDate(order.getOrderDate())
                        .totalPrice(order.getTotalPrice())
                        .orderItemImgLink(order.getOrderItem().getFirst().getVariant().getVariantImgLink())
                        .build()
        );} catch (Exception e) {
            Logging.handleException("OrderService", "getUserOrderHistory(String userId, int page, int size)", 369, generateErrorCode("06", "010"), e.getMessage());
            return null;
        }
    }

    private String generateOrderCode(){
        LocalDate today = LocalDate.now();
        String datePart = today.format(DATE_FORMATTER);

        Long countToday = orderRepo.countTodayOrder(today);

        long sequence = (countToday != null ? countToday : 0) + 1;

        String sequencePart = String.format("%04d", sequence);

        return "EC-" + datePart + "-" + sequencePart;
    }
}

