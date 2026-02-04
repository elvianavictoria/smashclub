package com.backendsyndicate.smashclub.admin.dto.request;

import com.backendsyndicate.smashclub.booking.model.EquipmentCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class ReqEquipmentSaveDTO {
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Equipment name must be alphanumeric")
    private String equipmentName;
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Brand must be alphanumeric")
    private String brand;
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Type must be alphanumeric")
    private String type;

    private BigDecimal price;
    private int stock;
    private String description = "";
    private int status;
    private EquipmentCategory equipmentCategory;
}
