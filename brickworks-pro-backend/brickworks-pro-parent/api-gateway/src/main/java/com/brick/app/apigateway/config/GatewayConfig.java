package com.brick.app.apigateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    /**
     * This is a Global Filter that runs on every request that passes through the gateway.
     * Its job is to find the 'Authorization' header (where the JWT is)
     * and pass it along to the downstream microservice.
     */
    @Bean
    public GlobalFilter customFilter() {
        return (exchange, chain) -> {
            // Get the original request from the exchange
            ServerWebExchange mutatedExchange = exchange;

            // Check if the Authorization header is present
            if (exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                // Get the header value
                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

                // Create a mutated (modified) request that includes this header
                mutatedExchange = exchange.mutate()
                        .request(r -> r.header(HttpHeaders.AUTHORIZATION, authHeader))
                        .build();
            }
            // Pass the mutated (or original) request down the filter chain
            return chain.filter(mutatedExchange);
        };
    }
}


