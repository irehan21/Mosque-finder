package com.mosquefinder.controller;

import com.mosquefinder.dto.UserDto;
import com.mosquefinder.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody UserDto.RegistrationRequest request) {
        userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Login user
     */
    @PostMapping("/login")
    public ResponseEntity<UserDto.AuthResponse> loginUser(@Valid @RequestBody UserDto.LoginRequest request) {
        UserDto.AuthResponse response = userService.loginUser(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Verify OTP
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<UserDto.AuthResponse> verifyOtp(@Valid @RequestBody UserDto.VerifyOtpRequest request) {
        UserDto.AuthResponse response = userService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user profile
     */
    @GetMapping("/profile")
    public ResponseEntity<UserDto.UserProfileResponse> getUserProfile(@RequestHeader("Authorization") String token) {
        // In a real app, you would extract userId from JWT token
        // For this example, let's assume we're getting it as a path variable
        String userId = "extractedFromToken";
        UserDto.UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Add mosque to favorites
     */
    @PostMapping("/favorites/{mosqueId}")
    public ResponseEntity<Void> addFavoriteMosque(
            @RequestHeader("Authorization") String token,
            @PathVariable String mosqueId) {
        // Extract userId from token
        String userId = "extractedFromToken";
        userService.addFavoriteMosque(userId, mosqueId);
        return ResponseEntity.ok().build();
    }

    /**
     * Remove mosque from favorites
     */
    @DeleteMapping("/favorites/{mosqueId}")
    public ResponseEntity<Void> removeFavoriteMosque(
            @RequestHeader("Authorization") String token,
            @PathVariable String mosqueId) {
        // Extract userId from token
        String userId = "extractedFromToken";
        userService.removeFavoriteMosque(userId, mosqueId);
        return ResponseEntity.ok().build();
    }
}