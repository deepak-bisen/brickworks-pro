package com.brick.app.customerservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "CUSTOMERS")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String customerId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String phone;

    @Column(unique = true)
    private String email;

    @Column
    private String address;

    // --- Getter and Setters ---
    public String getId() {
        return customerId;
    }

    public void setId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
