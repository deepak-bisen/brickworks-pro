package com.brick.app.customerservice.dto;

/**
 * DTO (Data Transfer Object) for validating and receiving contact form data.
 * This is a standard Java class (POJO).
 */
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
