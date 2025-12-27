package com.mosquefinder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "otp")  // ✅ MongoDB annotation (not @Entity)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name = "email_type_idx", def = "{'email': 1, 'type': 1}")
public class Otp {

    @Id
    private String id;

    @Field("email")
    private String email;

    @Field("otp")
    private String otp;

    @Field("expiry_time")
    private LocalDateTime expiryTime;

    @Field("created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}