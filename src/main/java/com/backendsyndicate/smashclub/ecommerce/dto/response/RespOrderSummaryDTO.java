package com.backendsyndicate.smashclub.ecommerce.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class RespOrderSummaryDTO {
    private Long orderId;
    private BigDecimal totalPrice;
    private byte status;
    private LocalDateTime orderDate;
}
