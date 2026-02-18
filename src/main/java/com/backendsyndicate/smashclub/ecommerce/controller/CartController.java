package com.backendsyndicate.smashclub.ecommerce.controller;

import com.backendsyndicate.smashclub.auth.service.AuthService;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.ecommerce.dto.request.ReqAddCartItemDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.request.ReqUpdateCartItemDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespCartDTO;
import com.backendsyndicate.smashclub.ecommerce.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.LoggingUtils;

/**
 *  Module Code - CART
 */

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<Object> getActiveCart(
            @RequestHeader("Authorization") String authorizationHeader,
            HttpServletRequest request) {
        String userId = extractUserIdFromToken(authorizationHeader);
        RespCartDTO cart = cartService.getOrCreateActiveCart(userId);
         return GlobalResponse.success("Cart retrieved", cart, request);
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addToCart(@RequestBody ReqAddCartItemDTO dto,
                                            @RequestHeader("Authorization") String authorizationHeader,
                                            HttpServletRequest request) {
        String userId = extractUserIdFromToken(authorizationHeader);
        RespCartDTO cart = cartService.addToCart(userId, dto);
        return GlobalResponse.success("Added to Cart!", cart, request);
    }

    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<Object> updateCartItem(@PathVariable Long cartItemId,
                                                 @RequestHeader("Authorization") String authorizationHeader,
                                                 @RequestBody ReqUpdateCartItemDTO dto,
                                                 HttpServletRequest request) {
        String userId = extractUserIdFromToken(authorizationHeader);
        RespCartDTO cart = cartService.updateCartItem(userId, dto);
        return GlobalResponse.success("Updated Cart!", cart, request);
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<Object> deleteCartItem(@PathVariable Long cartItemId,
                                                 @RequestHeader("Authorization") String authorizationHeader,
                                                 HttpServletRequest request) {
        String userId = extractUserIdFromToken(authorizationHeader);
        cartService.deleteCartItem(userId, cartItemId);
        return GlobalResponse.success("Cart item removed", null, request);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Object> clearCart(@RequestHeader("Authorization") String authorizationHeader,
                                                 HttpServletRequest request) {
        String userId = extractUserIdFromToken(authorizationHeader);
        cartService.clearCart(userId);
        return GlobalResponse.success("Cart cleared", null, request);
    }

    private String extractUserIdFromToken(String authorizationHeader) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                return authService.getUserIdFromToken(token);
            }
        } catch (Exception e) {
            Logging.handleException("CartController", "extractUserIdFromToken", 62, "CARTCONT-02-E-errorNo", e.getMessage());
        }
        return null;
    }
}
