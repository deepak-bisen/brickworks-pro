package com.brick.app.orderservice.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.List;

// This class represents the JSON object we expect to receive when a user wants to create an order.
@Data
public class OrderRequestDTO {
    private String customerId;
    private List<OrderItemRequestDTO> items;
    private String deliveryLocation;

    //for save customers when they request a quote
    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone must be between 10 digits")
    private String phone;

    @NotBlank(message = "email number is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "address number is required")
    private String address;
}
