package com.brick.app.orderservice.service;

import com.brick.app.orderservice.dto.OrderRequestDTO;
import com.brick.app.orderservice.entity.Order;

public interface OrderService {
    public Order createOrder(OrderRequestDTO orderRequest);
    public Order createPublicQuote(OrderRequestDTO orderRequest);
    Order createOrderWithStatus(OrderRequestDTO orderRequest, String status);
}
