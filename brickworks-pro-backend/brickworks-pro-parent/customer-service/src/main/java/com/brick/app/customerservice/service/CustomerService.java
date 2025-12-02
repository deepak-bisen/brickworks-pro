package com.brick.app.customerservice.service;

import com.brick.app.customerservice.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    public List<Customer> getAllCustomers();
    public Optional<Customer> createCustomer(Customer customer);
}
