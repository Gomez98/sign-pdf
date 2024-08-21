package com.goan.signmicroservice.handler;

import com.goan.signmicroservice.service.SignDocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Component
public class SignDocumentHandler {

    private final SignDocumentService signDocumentService;

    public SignDocumentHandler(SignDocumentService signDocumentService) {
        this.signDocumentService = signDocumentService;
    }

    public Mono<ServerResponse> signPdf(ServerRequest request) {
        return request.multipartData()
                .mapNotNull(multiValueMap -> multiValueMap.getFirst("file"))
                .cast(FilePart.class)
                .flatMap(signDocumentService::signPdf)
                .flatMap(msg -> ServerResponse.ok().bodyValue(msg))
                .onErrorResume(IllegalArgumentException.class, e -> ServerResponse.badRequest().bodyValue(e.getMessage()))
                .onErrorResume(IOException.class, e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(e.getMessage()));
    }
}
