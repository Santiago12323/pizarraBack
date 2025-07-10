package com.escuelaing.arsw.pizarraBack.infrastructure.repository;

import com.escuelaing.arsw.pizarraBack.infrastructure.repository.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}

