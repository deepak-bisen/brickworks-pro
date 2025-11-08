package com.brick.app.orderservice.controller;

import com.brick.app.orderservice.entity.Order;
import com.brick.app.orderservice.dto.OrderRequestDTO;
import com.brick.app.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.brick.app.orderservice.dto.OrderRequestDTO;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * This is the NEW public endpoint for the "Get a Quote" form.
     * It now calls the new service method to set the correct status.
     */
    @PostMapping("/public-quote")
    @ResponseStatus(HttpStatus.CREATED)
    public Order createPublicQuote(@RequestBody OrderRequestDTO orderRequest) {
        return orderService.createPublicQuote(orderRequest);
    }

    /**
     * This is the original, PROTECTED endpoint for an admin to create an order.
     * It will require a valid JWT.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order CreateOrder(@RequestBody OrderRequestDTO orderRequestDTO){
        return orderService.createOrder(orderRequestDTO);
    }



    /**
     * This is the NEW public endpoint for the "Get a Quote" form.
     * It is allowed by the SecurityConfig.
     * It reuses the same logic as our admin createOrder method.
     *
    @PostMapping("/public-quote")
    @ResponseStatus(HttpStatus.CREATED)
    public Order createPublicQuote(@RequestBody OrderRequestDTO orderRequest) {
        // We set the status to "New Request" to distinguish it from an admin-created order
        Order newOrder = orderService.createOrder(orderRequest);
        newOrder.setStatus("New Request");
        return newOrder;
    }
    */
}
