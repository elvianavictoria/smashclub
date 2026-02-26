package com.backendsyndicate.smashclub.admin.dto.response;

import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminOrderOrderItemDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminOrderUserDTO;
import com.backendsyndicate.smashclub.common.util.DatetimeFormatting;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
public class RespAdminOrderDetailDTO {
    private String orderCode;
    private BigDecimal subTotal = BigDecimal.ZERO;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private byte status = 0;
    private String statusDesc;
    private String orderDate;
    private String updatedAt;

    private List<RelAdminOrderOrderItemDTO> orderItem;
    private RelAdminOrderUserDTO user;

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
