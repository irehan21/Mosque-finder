package com.mosquefinder.controller;

import com.mosquefinder.dto.AuthenticationRequest;
import com.mosquefinder.dto.AuthenticationResponse;
import com.mosquefinder.dto.OtpVerificationRequest;
import com.mosquefinder.dto.RegisterRequest;
import com.mosquefinder.service.AuthService;
import com.mosquefinder.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final OtpService otpService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(Map.of(
                "message", "Registration successful. Please check your email for verification code."
        ));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest request) {
        boolean isVerified = authService.verifyOtp(request.getEmail(), request.getOtp());

        if (isVerified) {
            return ResponseEntity.ok(Map.of(
                    "message", "Email verified successfully. You can now log in."
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid or expired OTP"
            ));
        }
    }

    @PostMapping("/sendOtp/{email}")
    public ResponseEntity<?> sendOtp(@PathVariable String email) {
        boolean otpSent = otpService.generateAndSendOtp(email);  // Fix: Method should return boolean
        if (otpSent) {
            return ResponseEntity.ok(Map.of("message", "OTP sent successfully."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "User already verified or OTP sending failed."));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}