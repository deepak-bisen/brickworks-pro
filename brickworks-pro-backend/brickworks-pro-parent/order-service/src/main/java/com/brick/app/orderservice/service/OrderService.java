package com.brick.app.orderservice.service;

import com.brick.app.orderservice.dto.OrderDTO;
import com.brick.app.orderservice.dto.OrderRequestDTO;
import com.brick.app.orderservice.entity.Order;

import java.util.List;

public interface OrderService {
    public Order createOrder(OrderRequestDTO orderRequest);
    public Order createPublicQuote(OrderRequestDTO orderRequest);
    Order createOrderWithStatus(OrderRequestDTO orderRequest, String status);

    List<Order> getAllOrders();
    OrderDTO updateOrderStatus(Long orderId, String status);
}
