package org.oldvabik.apigateway.client;

import org.oldvabik.apigateway.dto.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthClient {
    private final WebClient webClient;

    public AuthClient(WebClient webClient) {
        this.webClient = webClient.mutate()
                .baseUrl("http://auth-service:8080")
                .build();
    }

    public Mono<RegisterResponse> register(RegisterRequest req) {
        return webClient.post()
                .uri("/api/v1/auth/register")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(RegisterResponse.class);
    }

    public Mono<AuthResponse> login(AuthRequest req) {
        return webClient.post()
                .uri("/api/v1/auth/login")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(AuthResponse.class);
    }

    public Mono<Boolean> validateToken(String token) {
        return webClient.post()
                .uri("/api/v1/auth/validate")
                .bodyValue(new ValidateTokenRequest(token))
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorReturn(false);
    }

    public Mono<Void> deleteCredentials(Long id) {
        return webClient.delete()
                .uri("/api/v1/auth/credentials/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(e -> Mono.empty());
    }
}
