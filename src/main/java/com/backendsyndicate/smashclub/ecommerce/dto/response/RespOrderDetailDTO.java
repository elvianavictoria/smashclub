package com.backendsyndicate.smashclub.ecommerce.dto.response;

import com.backendsyndicate.smashclub.common.util.DatetimeFormatting;
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
    private String orderDate;
    private String updatedAt;
    private byte refundStatus;
    private String refundRequestDate;
    private String refundStatusUpdateDate;
    private String paymentLink;
    private List<RespOrderItemDTO> items;

    public void setOrderDate(LocalDateTime orderDate) {
        if( orderDate != null ) {
            this.orderDate = DatetimeFormatting.getDatetimeFormat(orderDate);
        }
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        if( updatedAt != null ) {
            this.updatedAt = DatetimeFormatting.getDatetimeFormat(updatedAt);
        }
    }
}
