package com.brick.app.customer_service.repository;

import com.brick.app.customer_service.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
}
