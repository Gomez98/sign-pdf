package com.goan.api_gateway.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Document(collection = "user")
public record User(@MongoId ObjectId id, String email, String name, String password, List<String> roles) {
}
