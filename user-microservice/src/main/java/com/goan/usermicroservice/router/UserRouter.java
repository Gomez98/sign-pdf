package com.goan.usermicroservice.router;

import com.goan.usermicroservice.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> route(UserHandler handler){
        return RouterFunctions
                .route(RequestPredicates.GET("/api/v1/users")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::getAllUsers)
                .andRoute(RequestPredicates.GET("/api/v1/users/{id}")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::getUserById)
                .andRoute(RequestPredicates.POST("/api/v1/users")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::createUser);
    }
}
