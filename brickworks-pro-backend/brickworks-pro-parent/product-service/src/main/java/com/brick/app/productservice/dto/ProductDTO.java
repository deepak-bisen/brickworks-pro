package com.brick.app.productservice.dto;

import lombok.Data;

@Data
public class ProductDTO {

    private String name;
    private String color;
    private String brickType;
    private Double unitPrice;
    private String imageUrl;

}