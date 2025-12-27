package com.mosquefinder.controller;

import com.mosquefinder.dto.*;
import com.mosquefinder.exception.CustomException;
import com.mosquefinder.exception.TokenRefreshException;
import com.mosquefinder.service.AuthService;
import com.mosquefinder.service.OtpService;
import com.mosquefinder.model.OtpType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final OtpService otpService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            authService.register(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "success", true,
                            "message", "Registration successful. Please check your email for verification code."
                    ));

        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));

        } catch (Exception e) {
            log.error("Registration error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Registration failed. Please try again."
                    ));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest request) {
        boolean isVerified = authService.verifyOtp(request.getEmail(), request.getOtp());

        if (isVerified) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Email verified successfully. You can now log in."
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid or expired OTP"
            ));
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");

            // Rate limit check
            if (!otpService.canSendOtp(email)) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(Map.of(
                                "success", false,
                                "message", "Too many requests. Please try again after 1 hour."
                        ));
            }


            boolean otpSent = otpService.generateAndSendOtp(email);

            if (otpSent) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "OTP sent successfully."
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "User already verified or OTP sending failed."
                ));
            }
        } catch (Exception e) {
            log.error("Error resending OTP: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to send OTP. Please try again."
                    ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getStatus())
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        try {
            TokenRefreshResponse refreshResponse = authService.refreshToken(requestRefreshToken);
            return ResponseEntity.ok(refreshResponse);
        } catch (TokenRefreshException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authService.logout(userDetails);
        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Log out successful"
        ));
    }

    // ✅ NEW: Forgot Password
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            authService.forgotPassword(email);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "OTP sent to your email. Please check your inbox."
            ));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getStatus())
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            log.error("Error in forgot password: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to send OTP. Please try again."
                    ));
        }
    }

    // ✅ NEW: Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(
                    request.getEmail(),
                    request.getOtp(),
                    request.getNewPassword()
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Password reset successful. Please login with your new password."
            ));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getStatus())
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            log.error("Error in reset password: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to reset password. Please try again."
                    ));
        }
    }
}