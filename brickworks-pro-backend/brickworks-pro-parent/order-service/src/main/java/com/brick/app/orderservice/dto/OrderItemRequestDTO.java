package com.brick.app.orderservice.dto;

import lombok.Data;

@Data
public class OrderItemRequestDTO {
    private Long productId;
    private int quantity;

}
