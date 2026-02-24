package com.backendsyndicate.smashclub.admin.dto.relation;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class RelAdminBookingEquipmentDetailDTO {
    private RelAdminBookingEquipmentDTO equipment;
    private int quantity;
    private BigDecimal equipmentPrice;
}
