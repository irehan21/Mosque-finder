// src/main/java/com/mosquefinder/dto/TokenRefreshRequest.java
package com.mosquefinder.dto;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}