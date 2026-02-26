package com.backendsyndicate.smashclub.ecommerce.controller;

import com.backendsyndicate.smashclub.auth.service.AuthService;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.ecommerce.dto.request.ReqBuyNowDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespCreateOrderDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespOrderDetailDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespOrderSummaryDTO;
import com.backendsyndicate.smashclub.ecommerce.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    @Autowired
    private AuthService authService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<Object> createOrder(@RequestHeader("Authorization") String authorizationHeader,
            HttpServletRequest request){
        String userId = extractUserIdFromToken(authorizationHeader);
        RespCreateOrderDTO order = orderService.createOrder(userId);
        return GlobalResponse.success("Created order from cart", order, request);
    }

    @PostMapping("/buy-now")
    public ResponseEntity<Object> buyNow(@Valid @RequestBody ReqBuyNowDTO requestBody, @RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        String userId = extractUserIdFromToken(authorizationHeader);
        RespCreateOrderDTO order = orderService.buyNow(userId, requestBody);
        return GlobalResponse.success("Buy now order created", order, request);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Object> getOrderSummary(@PathVariable Long orderId, @RequestHeader("Authorization") String authorizationHeader,HttpServletRequest request) {
        String userId = extractUserIdFromToken(authorizationHeader);
        RespOrderDetailDTO response = orderService.getOrderDetail(orderId, userId);

        return GlobalResponse.success("Order summary retrieved", response, request);
    }

    @GetMapping("/history")
    public ResponseEntity<Object> getOrderHistory(@RequestParam(defaultValue = "0") int page,
                                                  @RequestHeader("Authorization") String authorizationHeader,
                                                  @RequestParam(defaultValue = "25") int size, HttpServletRequest request) {

        String userId = extractUserIdFromToken(authorizationHeader);

        Page<RespOrderSummaryDTO> response = orderService.getUserOrderHistory(userId, page, size);

        return GlobalResponse.success("Order history retrieved", response, request);
    }

    @PatchMapping("/{orderId}/update")
    public ResponseEntity<Object> updateOrderStatus(@PathVariable String orderCode, @RequestParam byte status, HttpServletRequest request) {
        orderService.updateOrderStatus(orderCode, status);
        return GlobalResponse.success("Order status updated", null, request);
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Object> cancelOrder(@PathVariable String orderCode, @RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        orderService.cancelOrder(orderCode);
        return GlobalResponse.success("Order cancelled", null, request);
    }

    @GetMapping("/{orderId}/refund-status")

    private String extractUserIdFromToken(String authorizationHeader) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                return authService.getUserIdFromToken(token);
            }
        } catch (Exception e) {
            Logging.handleException("CartController", "extractUserIdFromToken", 62, "ORDCONT-02-E-errorNo", e.getMessage());
        }
        return null;
    }
}
