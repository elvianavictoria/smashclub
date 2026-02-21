package com.backendsyndicate.smashclub.ecommerce.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class RespCreateOrderDTO {
    private Long orderId;
    private String userId;
    private BigDecimal totalPrice;
    private byte status;
    private String transactionId;
    private LocalDateTime orderDate;
}
