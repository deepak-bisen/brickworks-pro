package com.brick.app.customerservice.service;

import com.brick.app.customerservice.dto.ContactMessageRequest;

/**
 * Service interface for handling contact form logic.
 */
public interface ContactService {
    /**
     * Processes a new contact form submission.
     * @param request The validated contact form data.
     */
    void processContactMessage(ContactMessageRequest request);
}