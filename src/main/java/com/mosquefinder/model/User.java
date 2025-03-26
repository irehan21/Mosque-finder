package com.mosquefinder.model;

import com.mosquefinder.dto.UserDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "password is required")
    private String password;
    private boolean verified;
    private Locations location;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private List<String> favoriteMosques = new ArrayList<>();
    private String myMosques;
    private String roles;



    public UserDto toDto() {
        return new UserDto(
                this.id,
                this.name,
                this.email,
                this.verified,
                this.location != null ? this.location.toEntity() : null,
                this.createdAt,
                this.lastLoginAt,
                this.favoriteMosques,
                this.myMosques,
                this.roles
        );
    }
}