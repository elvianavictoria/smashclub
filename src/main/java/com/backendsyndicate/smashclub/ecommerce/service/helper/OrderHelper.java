package com.backendsyndicate.smashclub.ecommerce.service.helper;

import com.backendsyndicate.smashclub.ecommerce.dto.response.RespOrderDetailDTO;
import com.backendsyndicate.smashclub.ecommerce.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderHelper extends OrderService {
    @Override
    public RespOrderDetailDTO getOrderDetail(Long orderId, String userId) {
        return super.getOrderDetail(orderId, userId);
    }
}
