package com.mosquefinder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtpService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final long OTP_VALID_DURATION = 5; // 5 minutes

    /**
     * Generate a new OTP for the given mobile number
     */
    public String generateOtp(String mobileNumber) {
        // Generate a random 6-digit OTP
        String otp = generateRandomOtp(6);

        // Save the OTP to Redis with expiration
        String key = "OTP:" + mobileNumber;
        redisTemplate.opsForValue().set(key, otp, OTP_VALID_DURATION, TimeUnit.MINUTES);

        // TODO: In a real application, you would send this OTP via SMS
        log.info("Generated OTP for mobile {}: {}", mobileNumber, otp);

        return otp;
    }

    /**
     * Verify the OTP for the given mobile number
     */
    public boolean verifyOtp(String mobileNumber, String otp) {
        String key = "OTP:" + mobileNumber;
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp != null && storedOtp.equals(otp)) {
            // Delete the OTP once verified
            redisTemplate.delete(key);
            return true;
        }

        return false;
    }

    /**
     * Generate a random numeric OTP
     */
    private String generateRandomOtp(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10)); // 0-9
        }

        return otp.toString();
    }
}
