package com.mosquefinder.controller;

import com.mosquefinder.dto.UserDto;
import com.mosquefinder.model.Location;
import com.mosquefinder.model.User;
import com.mosquefinder.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserProfile(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Return 401 if not authenticated
        }

        String email = authentication.getName();
        userService.findByEmail(email);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/profile/location")
    public ResponseEntity<String> updateLocation(
            Authentication authentication,
            @RequestBody Location location) {

        String email = authentication.getName();
        User user = userService.findByEmail(email);
        User updatedUser = userService.updateLocation(user.getId(), location);

        return ResponseEntity.ok(userService.convertToDto(updatedUser));
    }

    @PostMapping("/mosques/{mosqueId}/favorite")
    public ResponseEntity<?> addFavoriteMosque(
            Authentication authentication,
            @PathVariable String mosqueId) {

        String email = authentication.getName();
        User user = userService.findByEmail(email);
        userService.addFavoriteMosque(user.getId(), mosqueId);

        return ResponseEntity.ok(Map.of(
                "message", "Mosque added to favorites"
        ));
    }

    @DeleteMapping("/mosques/{mosqueId}/favorite")
    public ResponseEntity<?> removeFavoriteMosque(
            Authentication authentication,
            @PathVariable String mosqueId) {

        String email = authentication.getName();
        User user = userService.findByEmail(email);
        userService.removeFavoriteMosque(user.getId(), mosqueId);

        return ResponseEntity.ok(Map.of(
                "message", "Mosque removed from favorites"
        ));
    }
}