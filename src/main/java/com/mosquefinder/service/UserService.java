package com.mosquefinder.service;

import com.mosquefinder.dto.UserDto;
import com.mosquefinder.exception.ResourceAlreadyExistsException;
import com.mosquefinder.exception.ResourceNotFoundException;
import com.mosquefinder.model.User;
import com.mosquefinder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final JwtService jwtService;

    /**
     * Register a new user
     */
    public void registerUser(UserDto.RegistrationRequest request) {
        // Check if mobile number is already in use
        if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new ResourceAlreadyExistsException("Mobile number already registered");
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setMobileNumber(request.getMobileNumber());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(new Date());

        userRepository.save(user);

        // Generate and send OTP for verification
        otpService.generateOtp(request.getMobileNumber());
    }

    /**
     * Login user
     */
    public UserDto.AuthResponse loginUser(UserDto.LoginRequest request) {
        // Find user by mobile number
        User user = userRepository.findByMobileNumber(request.getMobileNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Update last login time
        user.setLastLoginAt(new Date());
        userRepository.save(user);

        // Generate JWT token
        String token = jwtService.generateToken(user.getId(), user.getMobileNumber(), user.getRole());

        // Build response
        UserDto.AuthResponse response = new UserDto.AuthResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setMobileNumber(user.getMobileNumber());

        return response;
    }

    /**
     * Verify OTP
     */
    public UserDto.AuthResponse verifyOtp(UserDto.VerifyOtpRequest request) {
        boolean isValid = otpService.verifyOtp(request.getMobileNumber(), request.getOtp());

        if (!isValid) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        // Find user by mobile number
        User user = userRepository.findByMobileNumber(request.getMobileNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Generate JWT token
        String token = jwtService.generateToken(user.getId(), user.getMobileNumber(), user.getRole());

        // Build response
        UserDto.AuthResponse response = new UserDto.AuthResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setMobileNumber(user.getMobileNumber());

        return response;
    }

    /**
     * Get user profile
     */
    public UserDto.UserProfileResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserDto.UserProfileResponse response = new UserDto.UserProfileResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setMobileNumber(user.getMobileNumber());
        response.setEmail(user.getEmail());
        response.setFavoriteMosqueIds(user.getFavoriteMosqueIds());

        return response;
    }

    /**
     * Add mosque to favorites
     */
    public void addFavoriteMosque(String userId, String mosqueId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getFavoriteMosqueIds().contains(mosqueId)) {
            user.getFavoriteMosqueIds().add(mosqueId);
            userRepository.save(user);
        }
    }

    /**
     * Remove mosque from favorites
     */
    public void removeFavoriteMosque(String userId, String mosqueId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.getFavoriteMosqueIds().remove(mosqueId);
        userRepository.save(user);
    }
}
