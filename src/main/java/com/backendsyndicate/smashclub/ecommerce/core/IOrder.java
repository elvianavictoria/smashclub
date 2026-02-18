package com.backendsyndicate.smashclub.ecommerce.core;

import com.backendsyndicate.smashclub.ecommerce.dto.request.ReqBuyNowDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespCreateOrderDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespOrderDetailDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespOrderLogDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespOrderSummaryDTO;
import org.springframework.data.domain.Page;

public interface IOrder {
    public RespCreateOrderDTO createOrder(String userId);
    public RespCreateOrderDTO buyNow(String userId, ReqBuyNowDTO request);
    void updateOrderStatus(Long orderId, byte newStatus);
    void cancelOrder(Long orderId);
    public Page<RespOrderSummaryDTO> getUserOrderHistory(String userId, int page, int size);
    public RespOrderDetailDTO getOrderDetail(Long orderId, String userId);
}
