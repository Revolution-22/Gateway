package com.revolution.gateway.client;

import com.revolution.common.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthClient {

    private final WebClient.Builder webClientBuilder;


    @Value("${auth.service.url}")
    private String authServiceUrl;

    public Mono<UserResponse> validate(String token) {
        return webClientBuilder.build()
                .put()
                .uri(authServiceUrl + "/auth/validate?token=" + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new RuntimeException("Unauthorized")))
                .bodyToMono(UserResponse.class);
    }
}