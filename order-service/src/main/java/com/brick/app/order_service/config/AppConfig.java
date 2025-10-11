package com.brick.app.order_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration // Marks this class as a source of bean definitions
public class AppConfig {

    @Bean // This annotation tells Spring to create an instance of this object (a bean) and manage it.
    @LoadBalanced // This is the MOST IMPORTANT annotation. It enables client-side load balancing.
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}