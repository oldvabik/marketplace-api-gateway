package org.oldvabik.apigateway.filter;

import org.oldvabik.apigateway.client.AuthClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter implements GlobalFilter {

    private final AuthClient authClient;

    public JwtFilter(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        if (path.startsWith("/api/v1/auth/login") ||
                path.startsWith("/api/v1/auth/register") ||
                path.startsWith("/api/v1/auth/refresh")) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange);
        if (token == null) {
            return unauthorized(exchange, "Missing token");
        }

        return authClient.validateToken(token)
                .flatMap(isValid -> Boolean.TRUE.equals(isValid)
                        ? chain.filter(exchange)
                        : unauthorized(exchange, "Invalid or expired token"))
                .onErrorResume(e -> unauthorized(exchange, "Service unavailable"));
    }

    private String extractToken(ServerWebExchange exchange) {
        String header = exchange.getRequest().getHeaders().getFirst("Authorization");
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String json = String.format("{\"success\": false, \"message\": \"%s\"}", message);
        var buffer = exchange.getResponse().bufferFactory().wrap(json.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}