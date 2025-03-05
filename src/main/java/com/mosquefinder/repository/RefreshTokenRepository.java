// src/main/java/com/mosquefinder/repository/RefreshTokenRepository.java
package com.mosquefinder.repository;

import com.mosquefinder.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(String userId);
}