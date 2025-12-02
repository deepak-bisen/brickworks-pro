package com.brick.app.orderservice.service.impl;

import com.brick.app.orderservice.dto.CustomerDTO;
import com.brick.app.orderservice.dto.OrderDTO;
import com.brick.app.orderservice.dto.OrderRequestDTO;
import com.brick.app.orderservice.dto.ProductDTO;
import com.brick.app.orderservice.entity.Order;
import com.brick.app.orderservice.entity.OrderDetails;
import com.brick.app.orderservice.repository.OrderRepository;
import com.brick.app.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * This is the original, protected method for admins.
     * It sets the status to PENDING by default.
     */
    @Transactional
    // This annotation ensures that the entire method runs in a single database transaction. If any part fails, all database changes are rolled back.
    public Order createOrder(OrderRequestDTO orderRequest) {
        return createOrderWithStatus(orderRequest, "PENDING");
    }

    /**
     * This is the NEW method for the public quote form.
     * It allows us to set the status to "New Request".
     */
    @Transactional
    public Order createPublicQuote(OrderRequestDTO orderRequest) {
        // Here we explicitly set the status for a new public quote
        return createOrderWithStatus(orderRequest, "New Request");
    }

    /**
     * This is the main, reusable logic for creating any order.
     */
    public Order createOrderWithStatus(OrderRequestDTO orderRequest, String status) {

        String finalCustomerId = orderRequest.getCustomerId();

        // Step 1: If no ID provided, but we have details,
        // create/fetch customer if needed via Customer Service
        if (finalCustomerId == null || finalCustomerId.isEmpty() || finalCustomerId.equals("0") && orderRequest.getEmail() != null) {
            try {
                //prepare payload for Customer Service
                Map<String, String> customerPayload = new HashMap<>();
                customerPayload.put("name", orderRequest.getName());
                customerPayload.put("phone", orderRequest.getPhone());
                customerPayload.put("email", orderRequest.getEmail());

                // Default address to delivery location if creating new
                customerPayload.put("address", orderRequest.getDeliveryLocation());

                // Call Customer Service (Service Discovery handles the URL)
                CustomerDTO customerResponse = restTemplate.postForObject("http://customer-service/api/customers", customerPayload, CustomerDTO.class);

                if (customerResponse != null) {
                    finalCustomerId = customerResponse.getCustomerId();
                }
            } catch (Exception e) {
                System.err.println("Failed To Create Customer: " + e.getMessage());
                // Fallback: Set to a generic "Guest" ID if service fails
                finalCustomerId = "GUEST-ERROR";
            }
        }

        //Step 2: Create a new Order entity and set its basic properties.
        Order order = new Order();
        order.setCustomerId(finalCustomerId); // Use 0 for guest
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(status); // Use the status we passed in
        order.setDeliveryLocation(orderRequest.getDeliveryLocation());

        List<OrderDetails> orderDetails = new ArrayList<>();
        double totalCost = 0.0;

        // Step 3: Loop through each item in the incoming order request.
        // Process Items
        for (var itemRequest : orderRequest.getItems()) {
            // Step 3a: Call the product-service to get the product's current price and details.
            // We use the service name "product-service" in the URL. Eureka and @LoadBalanced handle the rest.
            String productUrl = "http://product-service/api/products/" + itemRequest.getProductId();

            // The RestTemplate makes the API call and automatically converts the JSON response into our ProductDTO object.
            ProductDTO product = restTemplate.getForObject(productUrl, ProductDTO.class);

            if (product == null) {
                // If the product doesn't exist in the product-service, we can't create the order.
                throw new IllegalArgumentException("Product not found with id: " + itemRequest.getProductId());
            }

            // Step 3b: Create a new OrderDetail entity for this line item.
            OrderDetails orderDetail = new OrderDetails();
            orderDetail.setProductId(itemRequest.getProductId());
            orderDetail.setQuantity(itemRequest.getQuantity());
            orderDetail.setPricePerUnit(product.getUnitPrice()); // Use the price fetched from the product-service
            orderDetail.setOrder(order); // Link the detail back to its parent order

            orderDetails.add(orderDetail);

            // Step 3c: Add this line item's cost to the running total.
            totalCost += itemRequest.getQuantity() * product.getUnitPrice();
        }

        // Step 4: Set the final calculated details on the order.
        order.setOrderDetails(orderDetails);
        order.setTotalCost(totalCost);

        // Step 5: Save the fully constructed Order object (along with its OrderDetails, thanks to cascading) to the database.
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {

        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "orderDate"));
    }

    @Override
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);

        return mapToDTO(savedOrder);
    }

    // --- Helper Method to convert Entity to DTO ---
    private OrderDTO mapToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setCustomerId(order.getCustomerId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotalCost(order.getTotalCost());
        dto.setDeliveryLocation(order.getDeliveryLocation());
        return dto;
    }
}