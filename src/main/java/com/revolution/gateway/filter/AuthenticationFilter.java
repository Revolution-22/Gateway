package com.revolution.gateway.filter;

import com.revolution.gateway.client.AuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GatewayFilter {

    private final AuthClient authClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (isPublicEndpoint(exchange)) {
            return chain.filter(exchange);
        }
        List<String> authorizationHeaders = exchange.getRequest().getHeaders().get("Authorization");
        if (authorizationHeaders != null) {
            String token = authorizationHeaders.get(0);
            if (token != null) {
                return authClient.validate(token)
                        .flatMap(_ -> chain.filter(exchange))
                        .onErrorResume(e -> {
                            e.printStackTrace();
                            return onError(exchange, HttpStatus.UNAUTHORIZED);
                        });
            }
        }
        return onError(exchange, HttpStatus.UNAUTHORIZED);
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private boolean isPublicEndpoint(ServerWebExchange exchange) {
        return exchange.getRequest().getURI().getPath().startsWith("/auth");
    }

}
