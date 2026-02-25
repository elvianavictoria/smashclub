package com.backendsyndicate.smashclub.admin.dto.response;

import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminOrderUserDTO;
import com.backendsyndicate.smashclub.common.util.DatetimeFormatting;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class RespAdminOrderListDTO {
    private long id;
    private String orderCode;
    private RelAdminOrderUserDTO user;
    private String createdAt;
    private BigDecimal totalPrice;
    private byte status;
    private String statusDesc = "";

    public void setCreatedAt(LocalDateTime createdAt) {
        if( createdAt != null ) {
            this.createdAt = DatetimeFormatting.getDatetimeFormat(createdAt);
        }
    }
}
