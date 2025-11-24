package com.brick.app.customerservice.controller;

import com.brick.app.customerservice.dto.ContactMessageRequest;
import com.brick.app.customerservice.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/contact") // Using /api/v1 for consistency
public class ContactController {

    private final ContactService contactService;

    // Constructor injection
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    /**
     * Public endpoint to receive "Contact Us" form submissions.
     */
    @PostMapping
    public ResponseEntity<?> submitContactForm(@Valid @RequestBody ContactMessageRequest request) {
        // The @Valid annotation triggers the validation rules in the DTO.
        // If validation fails, Spring Boot will automatically return a 400 Bad Request.

        contactService.processContactMessage(request);

        // Return a 200 OK response with a success message.
        return ResponseEntity.ok(Map.of("message", "Message received successfully!"));
    }
}