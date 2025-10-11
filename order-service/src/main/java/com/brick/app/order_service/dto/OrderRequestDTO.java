package com.brick.app.order_service.dto;

import com.brick.app.order_service.dto.OrderItemRequestDTO;

import java.util.List;

// This class represents the JSON object we expect to receive when a user wants to create an order.
public class OrderRequestDTO {
    private Long customerId;
    private List<OrderItemRequestDTO> items;

    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public List<OrderItemRequestDTO> getItems() { return items; }
    public void setItems(List<OrderItemRequestDTO> items) { this.items = items; }
}
