package com.mosquefinder.service;

import com.mosquefinder.dto.AuthenticationRequest;
import com.mosquefinder.dto.AuthenticationResponse;
import com.mosquefinder.dto.RegisterRequest;
import com.mosquefinder.model.User;
import com.mosquefinder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final UserRepository userRepository;

    public void register(RegisterRequest request) {
        // Check if user already exists
        if (userService.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already registered");
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


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Fetch user from DB to get latest 'verified' status
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credential");
        }

        if (!user.isVerified()) {
            throw new BadCredentialsException("User is not verified");
        }

        // ✅ Ensure fetching the latest user data before generating JWT
        String jwtToken = jwtService.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities(user.isVerified() ? "VERIFIED_USER" : "UNVERIFIED_USER")
                        .build()
        );

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .verified(user.isVerified())
                .userId(userService.convertToDto(user))
                .build();

    }

}