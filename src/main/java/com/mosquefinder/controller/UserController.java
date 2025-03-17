package com.mosquefinder.controller;

import com.mosquefinder.dto.LocationDto;
import com.mosquefinder.dto.UserDto;
import com.mosquefinder.model.User;
import com.mosquefinder.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Role;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Return 401 if not authenticated
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if user not found
        }

        // Convert the User entity to UserDto and return
        UserDto userDto = user.toDto();
        return ResponseEntity.ok(userDto);
    }


    @PutMapping("/profile/location")
    public ResponseEntity<?> updateLocation(Authentication authentication, @RequestBody LocationDto locationDto) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            userService.updateLocation(authentication.getName(), locationDto);
            return ResponseEntity.ok("Location updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating location");
        }
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

    @PostMapping("/updateRole")
    public ResponseEntity<?> updateRole(@RequestBody UserDto userDto,  Authentication authentication){
        userService.updateRole(authentication, userDto);
        return ResponseEntity.ok("Role updated successfully");

    }
}