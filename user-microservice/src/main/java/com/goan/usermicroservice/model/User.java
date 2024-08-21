package com.goan.usermicroservice.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "user")
public record User(@MongoId ObjectId id,String email, String name, String password) {
}
