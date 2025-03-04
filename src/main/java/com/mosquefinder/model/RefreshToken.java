// src/main/java/com/mosquefinder/model/RefreshToken.java
package com.mosquefinder.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "refreshTokens")
public class RefreshToken {
    @Id
    private String id;

    private String token;

    private String userId;

    private Instant expiryDate;
}