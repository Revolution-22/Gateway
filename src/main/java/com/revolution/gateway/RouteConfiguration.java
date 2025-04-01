package com.revolution.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfiguration {

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/auth/**")
                        .filters(f -> f.rewritePath("/auth/(?<segment>.*)", "/auth/${segment}")) // Rewrite path
                        .uri("lb://auth-service"))
                .route(p -> p
                        .path("/payments/**")
                        .filters(f -> f.rewritePath("/payments/(?<segment>.*)", "/payments/${segment}")) // Rewrite path
                        .uri("lb://payment-service"))
                .build();
    }

}
