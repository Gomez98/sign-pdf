package com.goan.api_gateway.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@AllArgsConstructor
public class JwtTokenFilter implements WebFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        return jwtTokenProvider.resolveToken(exchange.getRequest())
                .filterWhen(jwtTokenProvider::validateToken)
                .flatMap(jwtTokenProvider::getAuthentication)
                .doOnSuccess(auth -> {
                    ServerWebExchange mutableExchange = exchange.mutate().build();
                    mutableExchange.getAttributes().put(SecurityContext.class.getName(), new SecurityContextImpl(auth));
                })
                .then(chain.filter(exchange.mutate().build()));
    }
}
