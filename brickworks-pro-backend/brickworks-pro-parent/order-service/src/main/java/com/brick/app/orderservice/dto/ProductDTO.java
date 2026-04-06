package com.brick.app.orderservice.dto;

import lombok.Data;

// This class represents the simplified Product data the order-service needs from the product-service.
@Data
public class ProductDTO {
    private Long productId;
    private String name;
    private Double unitPrice;

}

