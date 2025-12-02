package com.brick.app.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ORDER_DETAILS")
public class OrderDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long odId;

    // This defines the "many" side of the relationship. Many OrderDetail items belong to one Order.
    // fetch = FetchType.LAZY means this data is only loaded from the DB when it's explicitly asked for.
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // We store the ID of the product from the product-service.
    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    // We store the price here to capture the price at the time of the order,
    // in case the product's price changes in the future.
    @Column(nullable = false)
    private Double pricePerUnit;

}
