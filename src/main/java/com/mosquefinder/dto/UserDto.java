package com.mosquefinder.dto;

import com.mosquefinder.model.Locations; // Ensure correct import
import com.mosquefinder.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String id;
    private String name;
    private String email;
    private boolean verified;
    private Locations location;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private List<String> favoriteMosques;
    private HashSet<String> roles;

    // ✅ Fixed toEntity() to match the User constructor
    public User toEntity() {
        return new User(
                this.id,
                this.name,
                this.email,
                null, // Exclude password for security
                this.verified,
                this.location,
                this.createdAt,
                this.lastLoginAt,
                this.favoriteMosques,
                this.roles
        );
    }
}
