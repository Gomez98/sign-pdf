package com.goan.documentmicroservice.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.MongoId;

@org.springframework.data.mongodb.core.mapping.Document(collection = "document")
public record Document(@MongoId ObjectId id, String userId, String name, String password) {
}
