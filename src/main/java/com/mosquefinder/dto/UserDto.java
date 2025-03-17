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
import java.util.Set;

@Data
@Builder
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
//    private HashSet<String> roles;
    private String roles;


//    public User toEntity() {
//        return new User(
//                this.id,
//                this.name,
//                this.email,
//                this.verified,
//                this.location != null ? this.location.toEntity() : null, // Ensure Location has a toDto() method
//                this.createdAt,
//                this.lastLoginAt,
//                this.favoriteMosques,
////                (HashSet<String>) this.roles
//                this.roles
//        );
//    }



}
