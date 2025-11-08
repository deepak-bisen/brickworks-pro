package com.brick.app.orderservice.config;

import com.brick.app.orderservice.config.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // Define authorization rules
                .authorizeHttpRequests(auth -> auth
                        // --- THIS IS THE FIX ---
                        // RULE 1: Allow all preflight OPTIONS requests from the browser.
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // RULE 2: Allow anyone to make a POST request to our new public quote endpoint.
                        .requestMatchers(HttpMethod.POST, "/api/orders/public-quote").permitAll()

                        // RULE 3: All other requests to this service (e.g., admin viewing orders) are protected.
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add the filter to validate tokens on protected routes
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}