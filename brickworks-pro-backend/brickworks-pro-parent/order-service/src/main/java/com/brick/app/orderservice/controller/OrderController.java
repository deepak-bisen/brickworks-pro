package com.brick.app.orderservice.controller;

import com.brick.app.orderservice.dto.OrderDTO;
import com.brick.app.orderservice.entity.Order;
import com.brick.app.orderservice.dto.OrderRequestDTO;
import com.brick.app.orderservice.service.OrderService;
import com.brick.app.orderservice.service.impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> createPublicQuote(@RequestBody OrderRequestDTO orderRequest) {
        try {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createPublicQuote(orderRequest));
        }catch (IllegalArgumentException e){
            //catching validation errors
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }catch (Exception e){
            //catching unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occured. Please try again later."));
        }
    }

    /**
     * This is the original, PROTECTED endpoint for an admin to create an order.
     * It will require a valid JWT.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order CreateOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        return orderService.createOrder(orderRequestDTO);
    }


    // --- NEW ENDPOINTS FOR ADMIN PANEL ---

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        try{
        return ResponseEntity.ok(orderService.getAllOrders());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> statusMap) {
        String status = statusMap.get("status");
        if (status == null || status.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Status is required"));
        }
        try {
            OrderDTO updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
