package com.mosquefinder.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UserDto {

    @Data
    public static class RegistrationRequest {
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        private String name;

        @NotBlank(message = "Mobile number is required")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Mobile number must be between 10-15 digits")
        private String mobileNumber;

        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "Mobile number is required")
        private String mobileNumber;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data
    public static class VerifyOtpRequest {
        @NotBlank(message = "Mobile number is required")
        private String mobileNumber;

        @NotBlank(message = "OTP is required")
        @Size(min = 4, max = 6, message = "OTP must be 4-6 characters")
        private String otp;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String userId;
        private String name;
        private String mobileNumber;
    }

    @Data
    public static class UserProfileResponse {
        private String id;
        private String name;
        private String mobileNumber;
        private String email;
        private List<String> favoriteMosqueIds;
    }
}