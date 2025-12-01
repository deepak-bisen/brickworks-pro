package com.brick.app.customerservice.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) for validating and receiving contact form data.
 * This is a standard Java class (POJO).
 */
@Data
public class ContactMessageRequest {

    private String name;

    private String email;

    private String message;

    // A no-argument constructor is needed for JSON deserialization (like by Spring Boot/Jackson)
    public ContactMessageRequest() {
    }

    // An all-argument constructor is useful for creating instances in code
    public ContactMessageRequest(String name, String email, String message) {
        this.name = name;
        this.email = email;
        this.message = message;
    }

    // --- Getters and Setters ---

}
