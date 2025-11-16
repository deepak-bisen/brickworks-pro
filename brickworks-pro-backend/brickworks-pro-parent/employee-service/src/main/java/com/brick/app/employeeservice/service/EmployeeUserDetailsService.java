package com.brick.app.employeeservice.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface EmployeeUserDetailsService {
    public UserDetails loadUserByUsername(String username);
}
