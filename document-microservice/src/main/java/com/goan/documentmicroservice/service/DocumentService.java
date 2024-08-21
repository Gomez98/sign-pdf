package com.goan.documentmicroservice.service;

import com.goan.documentmicroservice.model.Document;
import com.goan.documentmicroservice.repository.DocumentRepository;

import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final PasswordEncoder passwordEncoder;

    public Flux<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Mono<Document> getDocumentById(ObjectId id) {
        return documentRepository.findById(id);
    }

    public Mono<Document> createDocument(String userId, String name, String password) {
        String encryptedPassword = passwordEncoder.encode(password);
        return documentRepository.save(new Document(new ObjectId(), userId, name, encryptedPassword));
    }
}
