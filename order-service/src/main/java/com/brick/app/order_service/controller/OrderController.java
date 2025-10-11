package com.brick.app.order_service.controller;

import com.brick.app.order_service.entity.Order;
import com.brick.app.order_service.dto.OrderRequestDTO;
import com.brick.app.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order CreateOrder(@RequestBody OrderRequestDTO orderRequestDTO){
        return orderService.createOrder(orderRequestDTO);
    }

}
