package com.brick.app.customerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) for validating and receiving contact form data.
 * This is a standard Java class (POJO).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessageRequest {

    private String name;
    private String email;
    private String message;

}
