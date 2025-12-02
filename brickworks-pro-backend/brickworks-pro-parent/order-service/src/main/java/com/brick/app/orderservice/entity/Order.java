package com.brick.app.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "ORDERS")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    // We store only the ID of the customer, not a direct database link.
    // This is a core concept in microservices.
    @Column(nullable = false)
    private String customerId;

    // This is a relationship to OrderDetail within the SAME service's database.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderDetails> orderDetails = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false)
    private String status;

    @Column
    private Double totalCost;

    // --- NEW FIELD ---
    @Column(length = 500) // 500 characters for a full address
    private String deliveryLocation;

    }
