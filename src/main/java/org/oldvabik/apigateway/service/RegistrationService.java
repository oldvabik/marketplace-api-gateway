package org.oldvabik.apigateway.service;

import org.oldvabik.apigateway.client.AuthClient;
import org.oldvabik.apigateway.client.UserClient;
import org.oldvabik.apigateway.dto.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RegistrationService {
    private final AuthClient authClient;
    private final UserClient userClient;

    public RegistrationService(AuthClient authClient, UserClient userClient) {
        this.authClient = authClient;
        this.userClient = userClient;
    }

    public Mono<ApiResponse<UserDto>> register(RegisterForm form) {

        RegisterRequest authReq = RegisterRequest.builder()
                .email(form.getEmail())
                .password(form.getPassword())
                .role(form.getRole())
                .build();

        return authClient.register(authReq)
                .flatMap(registerResp -> {
                    Long authId = registerResp.getId();

                    AuthRequest loginReq = AuthRequest.builder()
                            .email(form.getEmail())
                            .password(form.getPassword())
                            .build();

                    return authClient.login(loginReq)
                            .flatMap(loginResp -> {
                                UserCreateRequest userReq = UserCreateRequest.builder()
                                        .name(form.getName())
                                        .surname(form.getSurname())
                                        .birthDate(form.getBirthDate())
                                        .email(form.getEmail())
                                        .build();

                                return userClient.createUser(userReq, loginResp.getAccessToken())
                                        .map(userDto -> ApiResponse.success("User registered successfully", userDto))
                                        .onErrorResume(error ->
                                                authClient.deleteCredentials(authId)
                                                        .then(Mono.just(ApiResponse.error("Registration failed: rolled back")))
                                        );
                            });
                })
                .onErrorResume(e -> Mono.just(ApiResponse.error("Failed to register in auth service")));
    }
}