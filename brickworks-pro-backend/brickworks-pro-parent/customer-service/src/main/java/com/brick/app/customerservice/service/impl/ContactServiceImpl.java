package com.brick.app.customerservice.service.impl;

import com.brick.app.customerservice.dto.ContactMessageRequest;
import com.brick.app.customerservice.entity.ContactMessage;
import com.brick.app.customerservice.repository.ContactMessageRepository;
import com.brick.app.customerservice.service.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {

    // Set up a logger to see the messages in the console
    private static final Logger log = LoggerFactory.getLogger(ContactServiceImpl.class);

    private final ContactMessageRepository contactMessageRepository;

    // Injecting the new repository
    public ContactServiceImpl(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    @Override
    @Transactional
    public void processContactMessage(ContactMessageRequest request) {
        // For now, we will just log the message.
        // In the future, this is where you would send an email or save to a different table.
        log.info("New contact message received! Processing...");
        log.info("From: {} ({})", request.getName(), request.getEmail());
        log.info("Message: {}", request.getMessage());

        // (Future enhancement: save this to a new 'ContactMessage' entity or email it)

        // Save the message to the database (Synchronous)
        ContactMessage contactMessage = new ContactMessage();
        contactMessage.setName(request.getName());
        contactMessage.setEmail(request.getEmail());
        contactMessage.setMessage(request.getMessage());
        contactMessage.setReceivedAt(LocalDateTime.now());
        contactMessage.setStatus("NEW");

        ContactMessage savedMessage = contactMessageRepository.save(contactMessage);
        log.info("Message saved to database with ID: {}", savedMessage.getId());
    }

    @Override
    public List<ContactMessage> getAllMessages() {
        return contactMessageRepository.findAll(Sort.by(Sort.Direction.DESC, "receivedAt"));
    }

    @Override
    public void deleteMessage(String id) {
        if (contactMessageRepository.existsById(id)) {
            contactMessageRepository.deleteById(id);
        } else {
            throw new RuntimeException("Message not found with id: " + id);
        }
    }

    @Override
    public void updateMessageStatus(String id, String status) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + id));

        message.setStatus(status);
        contactMessageRepository.save(message);
    }
}
