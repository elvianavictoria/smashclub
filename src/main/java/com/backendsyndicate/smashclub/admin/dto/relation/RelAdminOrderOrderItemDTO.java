package com.backendsyndicate.smashclub.admin.dto.relation;

import com.backendsyndicate.smashclub.ecommerce.model.Order;
import com.backendsyndicate.smashclub.ecommerce.model.ProductVariant;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class RelAdminOrderOrderItemDTO {
    private String productName;
    private int quantity = 0;
    private BigDecimal priceAtPurchase = BigDecimal.ZERO;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private RelAdminOrderItemVariantDTO variant;
}
