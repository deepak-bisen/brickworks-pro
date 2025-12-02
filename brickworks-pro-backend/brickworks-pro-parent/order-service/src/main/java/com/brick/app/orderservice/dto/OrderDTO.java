package com.brick.app.orderservice.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long orderId;
    private String customerId;
    private LocalDateTime orderDate;
    private String status;
    private Double totalCost;
    private String deliveryLocation;
    // You might want to include OrderDetailsDTOs here too if needed for the full view
    // private List<OrderDetailsDTO> orderDetails;
}