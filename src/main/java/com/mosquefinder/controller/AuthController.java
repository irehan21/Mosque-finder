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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwt = jwtService.generateToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Find user and update lastLoginAt
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Create refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return ResponseEntity.ok(new LoginResponse(
                jwt,
                refreshToken.getToken(),
                user.getId(),
                user.getEmail(),
                user.getName(),
                roles,
                user.isVerified()
        ));
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserId)
                .map(userId -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new TokenRefreshException("User not found for the refresh token"));

                    // Create an ad-hoc UserDetails
                    UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                            .username(user.getEmail())
                            .password("") // Not needed for token generation
                            .authorities(user.isVerified() ?
                                    List.of(new SimpleGrantedAuthority("VERIFIED_USER")) :
                                    List.of())
                            .build();

                    String token = jwtService.generateToken(userDetails);

                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException("Refresh token is not valid!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenService.deleteByUserId(user.getId());
        return ResponseEntity.ok().body(Map.of("message", "Log out successful"));
    }

}