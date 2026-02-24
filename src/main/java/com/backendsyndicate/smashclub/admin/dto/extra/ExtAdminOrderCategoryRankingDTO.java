package com.backendsyndicate.smashclub.admin.dto.extra;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ExtAdminOrderCategoryRankingDTO {
    private String category;
    private int soldQuantity;
}
