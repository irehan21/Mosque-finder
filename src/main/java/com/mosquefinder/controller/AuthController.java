package com.mosquefinder.controller;

import com.mosquefinder.dto.*;
import com.mosquefinder.exception.TokenRefreshException;
import com.mosquefinder.model.RefreshToken;
import com.mosquefinder.model.User;
import com.mosquefinder.repository.UserRepository;
import com.mosquefinder.service.AuthService;
import com.mosquefinder.service.JwtService;
import com.mosquefinder.service.OtpService;
import com.mosquefinder.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final OtpService otpService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;


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
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        // Set security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user details from authentication
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Delegate to service for business logic
        LoginResponse loginResponse = authService.handleSuccessfulLogin(userDetails);

        return ResponseEntity.ok(loginResponse);
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