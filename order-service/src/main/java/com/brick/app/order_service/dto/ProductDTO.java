package com.brick.app.order_service.dto;

// This class represents the simplified Product data the order-service needs from the product-service.
public class ProductDTO {
    private Long productId;
    private String name;
    private Double unitPrice;

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
}

