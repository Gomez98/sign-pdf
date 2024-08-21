package com.goan.usermicroservice.service;

import com.goan.usermicroservice.model.User;
import com.goan.usermicroservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Mono<User> getUserById(ObjectId id) {
        return userRepository.findById(id);
    }

    public Mono<User> createUser(String email, String name, String password) {
        return userRepository.save(new User(new ObjectId(), email, name, password));
    }
}
