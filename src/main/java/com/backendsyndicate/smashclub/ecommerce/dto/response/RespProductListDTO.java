package com.backendsyndicate.smashclub.ecommerce.dto.response;

import org.springframework.data.domain.Page;

public class RespProductListDTO {
    private Page<RespProductDTO> products;
    private Integer totalPages;
    private Integer totalElements;
    private Integer currentPage;
}
