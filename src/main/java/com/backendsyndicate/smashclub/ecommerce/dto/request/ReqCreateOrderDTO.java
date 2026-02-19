package com.backendsyndicate.smashclub.ecommerce.dto.request;

import java.util.List;

public class ReqCreateOrderDTO {
    private List<ReqOrderItemDTO> items;

    public List<ReqOrderItemDTO> getItems() {return items;}
    public void setItems(List<ReqOrderItemDTO> items) {this.items = items;}
}
