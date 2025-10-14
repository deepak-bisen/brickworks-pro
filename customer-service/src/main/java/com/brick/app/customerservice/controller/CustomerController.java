package com.brick.app.customerservice.controller;

import com.brick.app.customerservice.Entity.Customer;
import com.brick.app.customerservice.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping
    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)// Sets the HTTP status code to 201 Created on success
    public Customer createCustomer(@RequestBody Customer customer){  // @RequestBody tells Spring to convert the incoming JSON into a Product object

        return customerRepository.save(customer);
    }
}
