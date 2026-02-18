package com.backendsyndicate.smashclub.ecommerce.service;

import com.backendsyndicate.smashclub.auth.repository.UserRepository;
import com.backendsyndicate.smashclub.common.constant.CartStatusConstant;
import com.backendsyndicate.smashclub.ecommerce.core.ICart;
import com.backendsyndicate.smashclub.ecommerce.dto.request.ReqAddCartItemDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.request.ReqUpdateCartItemDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespCartDTO;
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
            Cart cart = cartRepo.findByUserIdAndStatus(userId, CartStatusConstant.CART_ACTIVE).orElseGet(()->{
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
            throw new RuntimeException(e);
        }
    }

    /**
     * Code: 02
     * @param userId
     * @param request
     * @return
     */
    @Override
    public RespCartDTO addToCart(String userId, ReqAddCartItemDTO request){
        Cart cart = getActiveCartEntity(userId);

        ProductVariant variant = productVariantRepo.findById(request.getVariantId()).orElseThrow(() -> new RuntimeException("Variant not found!"));

        CartItem cartItem = cartItemRepo.findByCartIdAndVariantId(cart.getId(), variant.getId())
                .orElse(new CartItem());

        cartItem.setCart(cart);
        cartItem.setVariant(variant);
        cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        cartItemRepo.save(cartItem);

        return modelMapper.map(cart, RespCartDTO.class);
    }

    /**
     * Code: 03
     * @param userId
     * @return
     */
    @Override
    public RespCartDTO updateCartItem(String userId, ReqUpdateCartItemDTO request) {
        Cart cart = getActiveCartEntity(userId);

        CartItem cartItem = cartItemRepo.findByIdAndCartId(request.getCartItemId(), cart.getId()).orElseThrow(() -> new RuntimeException("Item not found!"));

        if (request.getQuantity() <= 0) {
            cartItemRepo.delete(cartItem);
        } else {
            cartItem.setQuantity(request.getQuantity());
        }
        cartRepo.save(cart);

        return modelMapper.map(cart, RespCartDTO.class);
    }

    /**
     * Code: 04
     * @param userId
     * @param cartItemId
     * @return
     */
    @Override
    public String deleteCartItem(String userId, Long cartItemId) {
        Cart cart = getActiveCartEntity(userId);

        CartItem item = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId()))
            throw new RuntimeException("Unauthorized");

        cartItemRepo.delete(item);
        return "Item removed";
    }

    /**
     * Code: 05
     * @param userId
     * @return
     */
    @Override
    public String clearCart(String userId){
        Cart cart = getActiveCartEntity(userId);
        cartItemRepo.deleteById(cart.getId());
        return "Cart cleared";
    }

    private Cart getActiveCartEntity(String userId) {
        return cartRepo.findByUserIdAndStatus(userId, CartStatusConstant.CART_ACTIVE)
                .orElseThrow(() -> new RuntimeException("Active cart not found"));
    }

}
