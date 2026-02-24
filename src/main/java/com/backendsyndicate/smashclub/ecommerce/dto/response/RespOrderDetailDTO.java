package com.backendsyndicate.smashclub.ecommerce.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RespOrderDetailDTO {
    private Long orderId;
    private String orderCode;
    private byte status;
    private BigDecimal subtotal;
    private BigDecimal totalPrice;
    private LocalDateTime orderDate;

    private List<RespOrderItemDTO> items;
}
