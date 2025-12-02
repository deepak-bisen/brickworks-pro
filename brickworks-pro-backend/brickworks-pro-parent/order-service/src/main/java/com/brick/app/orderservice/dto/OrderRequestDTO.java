package com.brick.app.orderservice.dto;

import lombok.Data;

import java.util.List;

// This class represents the JSON object we expect to receive when a user wants to create an order.
@Data
public class OrderRequestDTO {
    private String customerId;
    private List<OrderItemRequestDTO> items;
    private String deliveryLocation;

    //for save customers when they request a quote
    private String name;
    private String phone;
    private String email;
    private String address;
}
