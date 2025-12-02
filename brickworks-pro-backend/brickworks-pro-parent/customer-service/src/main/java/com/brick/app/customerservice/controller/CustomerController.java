package com.brick.app.customerservice.controller;

import com.brick.app.customerservice.entity.Customer;
import com.brick.app.customerservice.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return new ResponseEntity<>(customerService.getAllCustomers(), HttpStatus.FOUND);
    }

    @PostMapping
    public ResponseEntity<Optional<Customer>> createCustomer(@RequestBody Customer customer) {  // @RequestBody tells Spring to convert the incoming JSON into a Product object
        return new ResponseEntity<>(customerService.createCustomer(customer), HttpStatus.CREATED);
    }
}
