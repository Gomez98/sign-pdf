package com.goan.documentmicroservice.repository;

import com.goan.documentmicroservice.model.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface DocumentRepository extends ReactiveMongoRepository<Document, ObjectId> {
}
