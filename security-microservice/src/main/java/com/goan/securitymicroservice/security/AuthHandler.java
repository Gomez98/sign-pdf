package com.goan.api_gateway.security;

import com.goan.api_gateway.client.UserClient;
import com.goan.api_gateway.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class AuthHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserClient userClient;

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(AuthRequest.class)
                .flatMap(authRequest ->
                        userClient.getUserByUsername(authRequest.username())
                                .flatMap(user -> {
                                    if (!authRequest.password().equals(user.password())) {
                                        return Mono.error(new RuntimeException("Invalid username or password"));
                                    }
                                    return jwtTokenProvider.createToken(user.name(), user.roles());
                                })
                                .flatMap(token -> {
                                    Map<Object, Object> model = new HashMap<>();
                                    model.put("username", authRequest.username());
                                    model.put("token", token);
                                    return ServerResponse.ok().bodyValue(model);
                                }));
    }
}
