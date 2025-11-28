package com.brick.app.productservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "PRODUCTS")
public class Product {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long productId;

    @Column(nullable = false)
    private String name;

    @Column
    private String color;

    @Column
    private String brickType;

    @Column(nullable = false)
    private Double unitPrice;

    @Column
    private int stockQuantity;

    @Column(length = 1024) // Allow for long URLs
    private String imageUrl;

 public Long getId() {
  return productId;
 }

 public void setId(Long productId) {
  this.productId = productId;
 }

 public String getName() {
  return name;
 }

 public void setName(String name) {
  this.name = name;
 }

 public String getColor() {
  return color;
 }

 public void setColor(String color) {
  this.color = color;
 }

 public String getBrickType() {
  return brickType;
 }

 public void setBrickType(String brickType) {
  this.brickType = brickType;
 }

 public Double getUnitPrice() {
  return unitPrice;
 }

 public void setUnitPrice(Double unitPrice) {
  this.unitPrice = unitPrice;
 }

 public int getStockQuantity() {
  return stockQuantity;
 }

 public void setStockQuantity(int stockQuantity) {
  this.stockQuantity = stockQuantity;
 }

 public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
