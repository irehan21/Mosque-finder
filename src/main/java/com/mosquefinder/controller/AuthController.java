package com.mosquefinder.controller;

import com.mosquefinder.dto.*;
import com.mosquefinder.exception.CustomException;
import com.mosquefinder.exception.TokenRefreshException;
import com.mosquefinder.service.AuthService;
import com.mosquefinder.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final OtpService otpService;
    private final AuthenticationManager authenticationManager;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getStatus()).body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        try {
            TokenRefreshResponse refreshResponse = authService.refreshToken(requestRefreshToken);
            return ResponseEntity.ok(refreshResponse);
        } catch (TokenRefreshException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authService.logout(userDetails);
        return ResponseEntity.ok().body(Map.of("message", "Log out successful"));
    }

}