package com.goan.documentmicroservice.router;

import com.goan.documentmicroservice.handler.DocumentHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class DocumentRouter {

    @Bean
    public RouterFunction<ServerResponse> route(DocumentHandler handler){
        return RouterFunctions
                .route(RequestPredicates.GET("/api/v1/documents")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::getAllDocuments)
                .andRoute(RequestPredicates.GET("/api/v1/documents/{id}")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::getDocumentById)
                .andRoute(RequestPredicates.POST("/api/v1/documents")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::createDocument);
    }
}
