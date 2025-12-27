package com.mosquefinder.service;

import com.mosquefinder.model.Otp;
import com.mosquefinder.model.OtpType;
import com.mosquefinder.model.User;
import com.mosquefinder.repository.OtpRepository;
import com.mosquefinder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final OtpRepository otpRepository;

    public boolean generateAndSendOtp(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!canSendOtp(email)) {
            return false;
        }

        String otpCode = generateOtp();

        otpRepository.deleteByEmail(email);

        Otp otp = Otp.builder()
                .email(email)
                .otp(otpCode)
                .createdAt(LocalDateTime.now())
                .expiryTime(LocalDateTime.now().plusMinutes(10))
                .build();

        otpRepository.save(otp);

        if (!user.isVerified()) {
            emailService.sendOtpEmail(email, otpCode);
        } else {
            emailService.sendPasswordResetOtp(email, otpCode);
        }

        return true;
    }

    public boolean verifyOtp(String email, String otpCode) {

        Otp otp = otpRepository.findByEmailAndOtp(email, otpCode)
                .orElse(null);

        if (otp == null) return false;

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            return false;
        }

        otpRepository.delete(otp);

        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null && !user.isVerified()) {
            user.setVerified(true);
            userRepository.save(user);
        }

        return true;
    }


    public boolean canSendOtp(String email) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long count = otpRepository.countByEmailAndCreatedAtAfter(email, oneHourAgo);
        return count < 3; // Max 3 OTPs per hour
    }


    public void deleteOtp(String email) {
        try {
            otpRepository.deleteByEmail(email);
        } catch (Exception e) {
            log.error("❌ Error deleting OTP for email: {}", email, e);
        }
    }


    private void verifyUser(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            user.setVerified(true);
            userRepository.save(user);
            log.info("✅ User verified: {}", email);
        }
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}