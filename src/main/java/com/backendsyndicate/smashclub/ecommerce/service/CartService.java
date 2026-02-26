package com.backendsyndicate.smashclub.ecommerce.service;

import com.backendsyndicate.smashclub.auth.repository.UserRepository;
import com.backendsyndicate.smashclub.common.constant.CartStatusConstant;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.ecommerce.core.ICart;
import com.backendsyndicate.smashclub.ecommerce.dto.request.ReqAddCartItemDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.request.ReqUpdateCartItemDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespCartDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespCartItemDTO;
import com.backendsyndicate.smashclub.ecommerce.model.Cart;
import com.backendsyndicate.smashclub.ecommerce.model.CartItem;
import com.backendsyndicate.smashclub.ecommerce.model.ProductVariant;
import com.backendsyndicate.smashclub.ecommerce.repo.CartItemRepo;
import com.backendsyndicate.smashclub.ecommerce.repo.CartRepo;
import com.backendsyndicate.smashclub.ecommerce.repo.ProductVariantRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * Module Code: CART
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CartService implements ICart {
    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private ProductVariantRepo productVariantRepo;

    @Autowired
    private UserRepository userRepo;

    private ModelMapper modelMapper = new ModelMapper();

    private String generateErrorCode(String methodNo, String errorNo) {
        return "CART-" + methodNo + "E" + errorNo;
    }

    /**
     * Code: 01
     * @param userId
     * @return
     */
    @Override
    public RespCartDTO getOrCreateActiveCart(String userId){
        assert userId != null;
        try {
            Cart cart = cartRepo.findByUserIdAndStatus(userId, CartStatusConstant.CART_ACTIVE)
                    .orElseGet(()->{
                Cart newCart = new Cart();
                newCart.setUser(userRepo.findById(userId).get());
                newCart.setStatus((byte) CartStatusConstant.CART_ACTIVE);
                newCart.setCartItems(new ArrayList<>());
                newCart.setCreatedAt(LocalDateTime.now());
                newCart.setTotalPrice(BigDecimal.ZERO);
                return cartRepo.save(newCart);
            });
            return modelMapper.map(cart, RespCartDTO.class);
        } catch (Exception e) {
            Logging.handleException("CartService", "getOrCreateActiveCart(String userId)", 73, generateErrorCode("01", "010"), e.getMessage());
            return null;
        }

    }

    /**
     * Code: 02
     * @param userId
     * @param request
     * @return
     */
    @Override
    public RespCartDTO addToCart(String userId, ReqAddCartItemDTO request) {
        Cart cart = getActiveCartEntity(userId);

        try {
            ProductVariant variant = productVariantRepo.findByIdAndSufficientStock(request.getVariantId(), request.getQuantity()).orElseThrow(() -> new RuntimeException("Variant not found!"));

            CartItem cartItem = cartItemRepo.findByCart_IdAndVariant_Id(cart.getId(), variant.getId())
                    .orElse(new CartItem());

            cartItem.setCart(cart);
            Logging.printConsole(variant.toString());
            cartItem.setVariant(variant);
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItemRepo.save(cartItem);
            cart.setTotalPrice(cart.getTotalPrice().add(variant.getPrice()).multiply(BigDecimal.valueOf(request.getQuantity())))  ;
            return modelMapper.map(cart, RespCartDTO.class);
        } catch (Exception e) {
            Logging.handleException("CartService", "addToCart(String userId, ReqAddCartItemDTO request)", 103, generateErrorCode("02", "010"), e.getMessage());
            return null;
        }
    }
    /**
     * Code: 03
     * @param userId
     * @return
     */
    @Override
    public RespCartDTO updateCartItem(String userId, ReqUpdateCartItemDTO request) {
        Cart cart = getActiveCartEntity(userId);

        try {
            CartItem cartItem = cartItemRepo.findByIdAndCart_Id(request.getCartItemId(), cart.getId()).orElseThrow(() -> new RuntimeException("Item not found!"));
            if (cartItem.getQuantity() + request.getQuantity() > cartItem.getVariant().getStock()){
                Logging.handleException("CartService", "updateCartItem", 119, generateErrorCode("02", "001"), "Not enough stock!");
                return null;
            }
        if (request.getQuantity() == 0) {
            cartItemRepo.delete(cartItem);
        } else {
            if (cartItem.getQuantity() < request.getQuantity()){
                cart.setTotalPrice(cart.getTotalPrice().add(cartItem.getVariant().getPrice().multiply(BigDecimal.valueOf(request.getQuantity()))));
            } else if (cartItem.getQuantity() > request.getQuantity()){
                cart.setTotalPrice(cart.getTotalPrice().subtract(cartItem.getVariant().getPrice().multiply(BigDecimal.valueOf(request.getQuantity()))));
            }
            cartItem.setQuantity(request.getQuantity());
        }
        cartRepo.save(cart);
        return modelMapper.map(cart, RespCartDTO.class);}
        catch (Exception e) {
            Logging.handleException("CartService", "updateCartItem(String userId, ReqUpdateCartItemDTO request)", 135, generateErrorCode("03", "010"), e.getMessage());
            return null;
        }
    }

    /**
     * Code: 04
     *
     * @param userId
     * @param cartItemId
     */
    @Override
    public void deleteCartItem(String userId, Long cartItemId) {
        Cart cart = getActiveCartEntity(userId);

        try{
        CartItem item = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId()))
        {throw new RuntimeException("Unauthorized");}

        cart.setTotalPrice(cart.getTotalPrice().subtract(item.getVariant().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))));
        cartItemRepo.delete(item);
        }
        catch (Exception e) {
            Logging.handleException("CartService", "deleteCartItem(String userId, Long cartItemId)", 161, generateErrorCode("04", "010"), e.getMessage());
        }
    }

    /**
     * Code: 05
     *
     * @param userId
     */
    @Override
    public void clearCart(String userId){
        Cart cart = getActiveCartEntity(userId);
        try {cartItemRepo.deleteByCart_Id(cart.getId());
            cart.setTotalPrice(BigDecimal.ZERO);
            }
        catch (Exception e) {
            Logging.handleException("CartService", "clearCart(userId)", 178, generateErrorCode("05", "010"), e.getMessage());
        }
    }

    /**
     * Code: 06
     * @param userId
     * @return
     */
    public RespCartDTO getActiveCart(String userId) {
        Cart cart = getActiveCartEntity(userId);
        RespCartDTO response = new RespCartDTO();
        try{
        response.setUserId(userId);
        response.setCartId(cart.getId());
        response.setStatus(cart.getStatus());
        response.setTotalPrice(cart.getTotalPrice());
        response.setCreatedAt(cart.getCreatedAt());
        ArrayList<RespCartItemDTO> cartItems = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            cartItems.add(modelMapper.map(cartItem, RespCartItemDTO.class));
        }
        response.setItems(cartItems);
        return response;}
        catch(Exception e) {
            Logging.handleException("CartService", "getActiveCart(String userId)", 203, generateErrorCode("06", "010"), e.getMessage());
            return null;
        }
    }

    /**
     * Code: 07
     * @param userId
     * @return
     */
    public Cart getActiveCartEntity(String userId) {
        return cartRepo.findByUserIdAndStatus(userId, CartStatusConstant.CART_ACTIVE)
                .orElseThrow(() -> new RuntimeException("Active cart not found"));
    }

}
