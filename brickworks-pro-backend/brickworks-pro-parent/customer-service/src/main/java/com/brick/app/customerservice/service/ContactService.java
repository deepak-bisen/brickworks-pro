package com.brick.app.customerservice.service;

import com.brick.app.customerservice.dto.ContactFormRequest;

/**
 * Service interface for handling contact form logic.
 */
public interface ContactService {
    /**
     * Processes a new contact form submission.
     * @param request The validated contact form data.
     */
    void processContactMessage(ContactFormRequest request);
}