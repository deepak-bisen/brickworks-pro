package com.brick.app.customerservice.service.impl;

import com.brick.app.customerservice.entity.Customer;
import com.brick.app.customerservice.repository.CustomerRepository;
import com.brick.app.customerservice.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> createCustomer(Customer customer) {
        log.info("Inside @Class CustomerServiceImpl inside @Method createCustomer: ");
        log.info("Creating new customer..");
        //1. check if customer already exist by email
        if (customer.getEmail() != null) {
            Optional<Customer> existingCustomer = customerRepository.findByEmail(customer.getEmail());
            if (existingCustomer.isPresent()) {
                //return the existing customer (idempotent behavior)
                return existingCustomer;
            }
        }
        // 2. if not, create one
        Customer saveCustomer = customerRepository.save(customer);
        log.info("Customer created successfully.");
        return Optional.of(saveCustomer);
    }

}
