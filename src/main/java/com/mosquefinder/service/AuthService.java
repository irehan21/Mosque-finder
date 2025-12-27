package com.mosquefinder.service;

import com.mosquefinder.dto.*;
import com.mosquefinder.exception.CustomException;
import com.mosquefinder.exception.TokenRefreshException;
import com.mosquefinder.model.OtpType;
import com.mosquefinder.model.RefreshToken;
import com.mosquefinder.model.User;
import com.mosquefinder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public void register(RegisterRequest request) {
        // Check if user already exists
        if (userService.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already registered");
        }

        // Prevent empty values from being stored
        if (request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (!request.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Create new user
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = userService.createUser(request.getName(), request.getEmail(), encodedPassword);

        // Send OTP for verification
        otpService.generateAndSendOtp(user.getEmail());
    }

    public boolean verifyOtp(String email, String otp) {
        boolean isValid = otpService.verifyOtp(email, otp);

        if (isValid) {
            userService.verifyUser(email);
        }

        return isValid;
    }

    public LoginResponse login(String email, String password) {
        // 1️⃣ Validate user credentials
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        // 2️⃣ ✅ CHECK: User verified hai ya nahi
        if (!user.isVerified()) {
            throw new CustomException(
                    "Please verify your email first. Check your inbox or request a new OTP.",
                    HttpStatus.FORBIDDEN
            );
        }

        // 3️⃣ Authenticate user with Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // 4️⃣ ✅ SINGLE DEVICE LOGIN: Delete old refresh tokens
        refreshTokenService.deleteByUserId(user.getId());

        // 5️⃣ Call handleSuccessfulLogin() to generate JWT & response
        return handleSuccessfulLogin((UserDetails) authentication.getPrincipal());
    }

    public LoginResponse handleSuccessfulLogin(UserDetails userDetails) {
        // Generate JWT token
        String jwt = jwtService.generateToken(userDetails);

        // Extract roles
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Find user and update last login time
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Create refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        // Create and return response
        return new LoginResponse(
                jwt,
                refreshToken.getToken(),
                user.getId(),
                user.getEmail(),
                user.getName(),
                roles,
                user.isVerified()
        );
    }

    public void logout(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenService.deleteByUserId(user.getId());
    }

    public TokenRefreshResponse refreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
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

                    return new TokenRefreshResponse(token, refreshToken);
                })
                .orElseThrow(() -> new TokenRefreshException("Refresh token is not valid!"));
    }

    // ✅ NEW: Forgot Password
    public void forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        if (!user.isVerified()) {
            log.info("verify emial first");
            throw new CustomException(
                    "Please verify your email first before resetting password",
                    HttpStatus.BAD_REQUEST
            );
        }

        // Rate limit check
        if (!otpService.canSendOtp(email)) {
            throw new CustomException(
                    "Too many requests. Please try again after 1 hour.",
                    HttpStatus.TOO_MANY_REQUESTS
            );
        }


        // Generate and send OTP
        otpService.generateAndSendOtp(email);
    }

    // ✅ NEW: Reset Password
    public void resetPassword(String email, String otp, String newPassword) {
        // Verify OTP
        boolean isValid = otpService.verifyOtp(email, otp);

        if (!isValid) {
            throw new CustomException("Invalid or expired OTP", HttpStatus.BAD_REQUEST);
        }

        // Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        // Validate new password
        if (newPassword.trim().isEmpty() || newPassword.length() < 6) {
            throw new CustomException("Password must be at least 6 characters", HttpStatus.BAD_REQUEST);
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // ✅ LOGOUT FROM ALL DEVICES: Delete all refresh tokens
        refreshTokenService.deleteByUserId(user.getId());
    }
}