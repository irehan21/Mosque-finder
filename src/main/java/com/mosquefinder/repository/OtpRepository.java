//package com.mosquefinder.repository;
//
//import org.springframework.data.mongodb.repository.MongoRepository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//public interface OtpRepository extends MongoRepository<OTP,String> {
//    List<OTP> findByCreationTimeBefore(LocalDateTime expiry);
//}