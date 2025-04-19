package com.revolution.gateway.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouteConfiguration {

    private final AuthenticationFilter authenticationFilter;

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/auth/**")
                        .filters(f -> f.filter(authenticationFilter).rewritePath("/auth/(?<segment>.*)", "/auth/${segment}"))
                        .uri("lb://auth-service"))
                .route(p -> p
                        .path("/payments/**")
                        .filters(f -> f.filter(authenticationFilter).rewritePath("/payments/(?<segment>.*)", "/payments/${segment}")) // Rewrite path
                        .uri("lb://payment-service"))
                .route(p -> p
                        .path("/details/**")
                        .filters(f -> f.filter(authenticationFilter).rewritePath("/details/(?<segment>.*)", "/details/${segment}")) // Rewrite path
                        .uri("lb://details-service"))
                .route(p -> p
                        .path("/admin/**")
                        .filters(f -> f.filter(authenticationFilter).rewritePath("/admin/(?<segment>.*)", "/admin/${segment}")) // Rewrite path
                        .uri("lb://admin-service"))
                .build();
    }

}
