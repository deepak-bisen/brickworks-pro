package com.brick.app.customerservice.service;

import com.brick.app.customerservice.dto.ContactMessageRequest;
import com.brick.app.customerservice.entity.ContactMessage;

import java.util.List;

/**
 * Service interface for handling contact form logic.
 */
public interface ContactService {
    /**
     * Processes a new contact form submission.
     * @param request The validated contact form data.
     */
    void processContactMessage(ContactMessageRequest request);

    List<ContactMessage> getAllMessages();

    void deleteMessage(String id);

    void updateMessageStatus(String id, String status);
}