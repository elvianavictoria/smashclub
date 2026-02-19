package com.backendsyndicate.smashclub.ecommerce.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;


@Data
@Getter
@Setter
public class RespCartDTO {
    private String userId;
    private ArrayList<RespCartItemDTO> items;
    private BigDecimal totalPrice;
    private byte status;
    private LocalDateTime createdAt;
}
