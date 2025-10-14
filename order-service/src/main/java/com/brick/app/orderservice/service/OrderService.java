package com.brick.app.orderservice.service;

import com.brick.app.orderservice.dto.OrderRequestDTO;
import com.brick.app.orderservice.dto.ProductDTO;
import com.brick.app.orderservice.entity.Order;
import com.brick.app.orderservice.entity.OrderDetails;
import com.brick.app.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional // This annotation ensures that the entire method runs in a single database transaction. If any part fails, all database changes are rolled back.
    public Order createOrder(OrderRequestDTO orderRequest) {
        // Step 1: Create a new Order entity and set its basic properties.
        Order order = new Order();
        order.setCustomerId(orderRequest.getCustomerId());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        List<OrderDetails> orderDetails = new ArrayList<>();
        double totalCost = 0.0;

        // Step 2: Loop through each item in the incoming order request.
        for (var itemRequest : orderRequest.getItems()) {
            // Step 2a: Call the product-service to get the product's current price and details.
            // We use the service name "product-service" in the URL. Eureka and @LoadBalanced handle the rest.
            String productUrl = "http://product-service/api/products/" + itemRequest.getProductId();

            // The RestTemplate makes the API call and automatically converts the JSON response into our ProductDTO object.
            ProductDTO product = restTemplate.getForObject(productUrl, ProductDTO.class);

            if (product == null) {
                // If the product doesn't exist in the product-service, we can't create the order.
                throw new IllegalArgumentException("Product not found with id: " + itemRequest.getProductId());
            }

            // Step 2b: Create a new OrderDetail entity for this line item.
            OrderDetails orderDetail = new OrderDetails();
            orderDetail.setProductId(itemRequest.getProductId());
            orderDetail.setQuantity(itemRequest.getQuantity());
            orderDetail.setPricePerUnit(product.getUnitPrice()); // Use the price fetched from the product-service
            orderDetail.setOrder(order); // Link the detail back to its parent order

            orderDetails.add(orderDetail);

            // Step 2c: Add this line item's cost to the running total.
            totalCost += itemRequest.getQuantity() * product.getUnitPrice();
        }

        // Step 3: Set the final calculated details on the order.
        order.setOrderDetails(orderDetails);
        order.setTotalCost(totalCost);

        // Step 4: Save the fully constructed Order object (along with its OrderDetails, thanks to cascading) to the database.
        return orderRepository.save(order);
    }
}

