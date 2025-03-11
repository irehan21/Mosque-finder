package com.mosquefinder.service;

import com.mosquefinder.model.User;
import com.mosquefinder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            log.warn("User with email {} not found!", email);
            return false;  // User does not exist
        }
        String otp = generateOtp();
        otpCache.put(email, otp);
        otpTimestamps.put(email, System.currentTimeMillis());
        emailService.sendOtpEmail(email, otp);
        log.info("Generated OTP for email {}: {}", email, otp);
        return true;
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
