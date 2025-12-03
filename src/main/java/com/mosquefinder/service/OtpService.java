package com.mosquefinder.service;

import com.mosquefinder.model.User;
import com.mosquefinder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {
    private final EmailService emailService;
    private final UserRepository userRepository;
    private static final long OTP_VALID_DURATION = 5 * 60 * 1000; // 5 minutes
    private final ConcurrentHashMap<String, String> otpCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> otpTimestamps = new ConcurrentHashMap<>();


    public boolean generateAndSendOtp(String email) {
        try {
            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                log.warn("User with email {} not found!", email);
                return false;
            }

            String otp = generateOtp();
            otpCache.put(email, otp);
            otpTimestamps.put(email, System.currentTimeMillis());

            log.info("Generated OTP for email {}: {}", email, otp);

            // Try to send email - but don't fail if it times out
            try {
                emailService.sendOtpEmail(email, otp);
                log.info("✅ OTP email sent successfully to: {}", email);
            } catch (Exception emailException) {
                log.error("❌ Failed to send OTP email to: {} - but OTP is stored", email, emailException);
                // OTP is still generated and stored, just email failed
            }

            return true;  // OTP generated successfully (email may or may not have been sent)

        } catch (Exception e) {
            log.error("❌ Error in generateAndSendOtp for email: {}", email, e);
            return false;
        }
    }


    public boolean verifyOtp(String email, String otp) {
        String storedOtp = otpCache.get(email);
        Long timestamp = otpTimestamps.get(email);

        if (storedOtp != null && storedOtp.equals(otp)) {
            long currentTime = System.currentTimeMillis();
            if (timestamp != null && (currentTime - timestamp) <= OTP_VALID_DURATION) {
                otpCache.remove(email);
                otpTimestamps.remove(email);
                verifyUser(email);
                return true;
            } else {
                otpCache.remove(email);
                otpTimestamps.remove(email);
            }
        }
        return false;
    }

    private void verifyUser(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            user.setVerified(true);
            userRepository.save(user);
        }
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

}
