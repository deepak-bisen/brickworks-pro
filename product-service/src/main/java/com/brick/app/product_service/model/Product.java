package com.brick.app.product_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "PRODUCTS")
public class Product {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

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

 public Long getId() {
  return id;
 }

 public void setId(Long id) {
  this.id = id;
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
}
