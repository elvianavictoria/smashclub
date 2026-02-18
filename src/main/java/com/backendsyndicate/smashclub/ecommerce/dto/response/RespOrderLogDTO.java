package com.backendsyndicate.smashclub.ecommerce.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class RespOrderLogDTO {
    private Long orderId;
    private byte status;
    private LocalDateTime orderDate;
    private BigDecimal totalPrice;
}
