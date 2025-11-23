package com.brick.app.employeeservice.service.impl;

import com.brick.app.employeeservice.entity.Employee;
import com.brick.app.employeeservice.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class EmployeeUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //find the employee in our database using the custom repository method.

        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: "+username));

        // Convert our Employee object into a Spring Security UserDetails object
        // The User object take username, password, and a list of authorities/role.
        return new User(employee.getUsername(), employee.getPassword(), new ArrayList<>());
    }
}
