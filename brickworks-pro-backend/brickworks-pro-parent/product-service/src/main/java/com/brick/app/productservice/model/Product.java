package com.brick.app.productservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PRODUCTS")
@Data
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

    @Column(nullable = false)
    private int stockQuantity;

    @Column
    private String imageName;

    @Column
    private String imageType;

    @Lob
    @Column(name = "image_data", columnDefinition = "LONGBLOB")
    private byte[] imageData;
}
