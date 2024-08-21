package com.goan.documentmicroservice.handler;

import com.goan.documentmicroservice.model.Document;
import com.goan.documentmicroservice.service.DocumentService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@AllArgsConstructor
public class DocumentHandler {

    private final DocumentService documentService;

    public Mono<ServerResponse> getAllDocuments(ServerRequest request) {
        return ServerResponse.ok().body(documentService.getAllDocuments(), Document.class);
    }

    public Mono<ServerResponse> getDocumentById(ServerRequest request) {
        String documentId = request.pathVariable("id");
        return documentService.getDocumentById(new ObjectId(documentId))
                .flatMap(document -> ServerResponse.ok().body(fromValue(document)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createDocument(ServerRequest request) {
        Mono<Document> documentMono = request.bodyToMono(Document.class);
        return documentMono.flatMap(document -> documentService.createDocument(document.userId(), document.name(), document.password())
                .flatMap(createdUser -> ServerResponse.status(HttpStatus.CREATED).body(fromValue(createdUser))));
    }
}
