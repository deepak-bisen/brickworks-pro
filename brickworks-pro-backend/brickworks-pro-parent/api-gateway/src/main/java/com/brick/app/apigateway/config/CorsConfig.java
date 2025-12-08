package com.brick.app.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {

        CorsConfiguration corsConfig = new CorsConfiguration();

        /**
         * // This allows requests from any origin.
         * corsConfig.setAllowedOrigins(List.of("*"));
         * // This allows all HTTP methods (GET, POST, DELETE, etc.)
         * corsConfig.setAllowedMethods(List.of("*"));
         * // This allows all headers (including our 'Authorization' header)
         * corsConfig.setAllowedHeaders(List.of("*"));
         */


        // --- This is the key change ---
        // We are explicitly allowing your frontend's origin
        corsConfig.setAllowedOrigins(Arrays.asList("http://127.0.0.1:5500", "http://localhost:5500", "http://127.0.0.1:5501 "));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L); // Cache preflight response for 1 hour
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        corsConfig.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization"));


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig); // Apply this to all routes

        return new CorsWebFilter(source);
    }
}
