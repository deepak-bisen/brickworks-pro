package com.brick.app.customerservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "CONTACT_MESSAGES")
@Data
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 5000)
    private String message;

    @Column(nullable = false)
    private LocalDateTime receivedAt;

    @Column(nullable = false, length = 50)
    private String status; // e.g., "NEW", "READ", "ARCHIVED"

}