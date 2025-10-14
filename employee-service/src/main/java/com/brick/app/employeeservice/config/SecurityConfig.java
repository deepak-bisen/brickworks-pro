package com.brick.app.employeeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity   //Enables Spring Security's web security support
public class SecurityConfig {

    //this is the main configuration method for security
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http)  throws Exception{
        http
                // 1. Disable CSRF protection. this is common for all stateless REST APIs.
                .csrf(csrf -> csrf.disable())

                // 2. Define authorization rules
                .authorizeHttpRequests(auth-> auth
                        //allow anyone to access the login and register endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        //all other requests must be authenticated
                        .anyRequest().authenticated()
                )
                // 3. Configure session management to be stateless.
                // This is crucial for JWT, as we are not using traditional sessions.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                //we will add our JWT filter here in a later step

        return http.build();
    }

    //This bean is used for hashing passwords.
    @Bean
    public PasswordEncoder passwordEncoder(){
        //BCrypt is a strong, widely-used hashing algorithm.
        return new BCryptPasswordEncoder();
    }

    // This bean is required to inject the AuthenticationManager in our AuthController.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    }
