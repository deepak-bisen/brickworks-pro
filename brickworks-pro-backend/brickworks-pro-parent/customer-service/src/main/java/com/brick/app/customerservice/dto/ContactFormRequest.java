package com.brick.app.customerservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) for validating and receiving contact form data.
 * This is a standard Java class (POJO).
 */
public class ContactFormRequest {

    @NotEmpty(message = "Name cannot be empty")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotEmpty(message = "Message cannot be empty")
    @Size(min = 5, max = 5000, message = "Message must be between 10 and 5000 characters")
    private String message;

    // A no-argument constructor is needed for JSON deserialization (like by Spring Boot/Jackson)
    public ContactFormRequest() {
    }

    // An all-argument constructor is useful for creating instances in code
    public ContactFormRequest(String name, String email, String message) {
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
