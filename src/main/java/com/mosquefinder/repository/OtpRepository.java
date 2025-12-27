package com.mosquefinder.repository;

import com.mosquefinder.model.Otp;
import com.mosquefinder.model.OtpType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends MongoRepository<Otp, String> {

    Optional<Otp> findByEmailAndOtp(String email, String otp);

    long countByEmailAndCreatedAtAfter(String email, LocalDateTime time);

    void deleteByEmail(String email);

    Optional<Otp> findFirstByEmailOrderByCreatedAtDesc(String email);
}

