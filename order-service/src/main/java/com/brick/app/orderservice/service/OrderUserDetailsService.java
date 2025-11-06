package com.brick.app.orderservice.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class OrderUserDetailsService implements UserDetailsService {
    // This is a simplified version for services that only validate tokens.
    // It doesn't need to check a database. It just creates a UserDetails object
    // from the username that was extracted from the token.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // The password field is not checked here, so it can be empty.
        return new User(username, "", new ArrayList<>());
    }
}