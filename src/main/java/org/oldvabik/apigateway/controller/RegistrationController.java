package org.oldvabik.apigateway.controller;

import jakarta.validation.Valid;
import org.oldvabik.apigateway.dto.ApiResponse;
import org.oldvabik.apigateway.dto.RegisterForm;
import org.oldvabik.apigateway.dto.UserDto;
import org.oldvabik.apigateway.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<ApiResponse<UserDto>>> register(@Valid @RequestBody RegisterForm form) {
        return registrationService.register(form)
                .map(response -> response.isSuccess()
                        ? ResponseEntity.status(201).body(response)
                        : ResponseEntity.badRequest().body(response));
    }
}
