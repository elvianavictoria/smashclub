package com.backendsyndicate.smashclub.admin.dto.response;

import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminEquipmentEquipmentCategoryDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class RespAdminEquipmentDetailDTO {
    private Long id;
    private String equipmentName;
    private String brand;
    private String type;
    private BigDecimal price;
    private int stock;
    private int status;
    private RelAdminEquipmentEquipmentCategoryDTO equipmentCategory;
}
