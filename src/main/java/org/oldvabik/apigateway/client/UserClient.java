package org.oldvabik.apigateway.client;

import org.oldvabik.apigateway.dto.UserCreateRequest;
import org.oldvabik.apigateway.dto.UserDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserClient {
    private final WebClient webClient;

    public UserClient(WebClient webClient) {
        this.webClient = webClient.mutate()
                .baseUrl("http://user-service:8081")
                .build();
    }

    public Mono<UserDto> createUser(UserCreateRequest req, String token) {
        return webClient.post()
                .uri("/api/v1/users")
                .header("Authorization", "Bearer " + token)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(UserDto.class);
    }
}
