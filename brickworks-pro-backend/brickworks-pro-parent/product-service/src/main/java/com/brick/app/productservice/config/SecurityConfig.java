/*package com.brick.app.productservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Make sure this import exists
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter; // Ensure this filter class exists in this project

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // RULE: Allow anyone to perform a GET request to see products.
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        // RULE: For any other request (POST, PUT, DELETE), the user must be authenticated.
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Ensuring the filter is added
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

*/

package com.brick.app.productservice.config;

import com.brick.app.productservice.service.JwtUtil;
import com.brick.app.productservice.service.impl.ProductUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final ProductUserDetailsServiceImpl userDetailsService;

    // Inject dependencies directly into config
    public SecurityConfig(JwtUtil jwtUtil, ProductUserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Creating the filter manually to avoid circular dependency issues with auto-wiring
        JwtRequestFilter jwtRequestFilter = new JwtRequestFilter(userDetailsService, jwtUtil);

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // RULE 1: Allow ANYONE (public) to view products
                        // --- THIS IS THE CORRECTED LINE ---
                        // It now matches /api/products AND /api/products/1, /api/products/2, etc.
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        // All other requests (POST, PUT, DELETE) are still protected.
                        // RULE 2: Allow ONLY authenticated users (admin) to create, update, or delete products.
                        .requestMatchers(HttpMethod.POST, "/api/products").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").authenticated()

                        // RULE 3: All other requests (if any) are denied by default.
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}