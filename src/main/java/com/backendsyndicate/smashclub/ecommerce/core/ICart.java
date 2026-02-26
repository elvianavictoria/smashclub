package com.backendsyndicate.smashclub.ecommerce.core;

import com.backendsyndicate.smashclub.ecommerce.dto.request.ReqAddCartItemDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.request.ReqUpdateCartItemDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespCartDTO;

public interface ICart {
    public RespCartDTO getOrCreateActiveCart(String userId);
    public RespCartDTO addToCart(String userId, ReqAddCartItemDTO request);
    public RespCartDTO updateCartItem(String userId, ReqUpdateCartItemDTO request);
    void deleteCartItem(String userId, Long cartItemId);
    void clearCart(String userId);
}
