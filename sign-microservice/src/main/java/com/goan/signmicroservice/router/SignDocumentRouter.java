package com.goan.signmicroservice.router;

import com.goan.signmicroservice.handler.SignDocumentHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SignDocumentRouter {

    @Bean
    public RouterFunction<ServerResponse> route(SignDocumentHandler handler){
        return RouterFunctions
                .route(RequestPredicates.POST("/api/v1/sign/document")
                        .and(RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA)), handler::signPdf);
    }
}
