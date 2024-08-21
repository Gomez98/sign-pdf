package com.goan.securitymicroservice.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final ReactiveAuthenticationManager authenticationManager;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authenticationManager(authenticationManager)
                .authorizeExchange(exchanges ->
                        exchanges
                                .pathMatchers("/auth/**", "/actuator/**")
                                .permitAll()
                                .anyExchange().authenticated()
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> {
                                    swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                }))
                                .accessDeniedHandler((swe, e) -> Mono.fromRunnable(() -> {
                                    swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                                }))
                )
                .build();
    }
}
