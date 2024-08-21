package com.goan.signmicroservice.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "digital_sign")
public record DigitalSign(@MongoId Object id, String userId, String password, String name, String ext) {
}
