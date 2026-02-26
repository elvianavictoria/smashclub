package com.backendsyndicate.smashclub.ecommerce.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RespOrderDetailDTO {
    private Long orderId;
    private String orderCode;
    private byte status;
    private BigDecimal subtotal;
    private BigDecimal totalPrice;
    private LocalDateTime orderDate;
    private LocalDateTime updatedAt;
    private byte refundStatus;
    private LocalDateTime refundRequestDate;
    private LocalDateTime refundStatusUpdateDate;
    private String paymentLink;
    private List<RespOrderItemDTO> items;
}
