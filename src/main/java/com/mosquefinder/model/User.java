package com.mosquefinder.model;

import com.mosquefinder.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.mosquefinder.model.Locations;
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
    private String name;
    private String email;
    private String password;
    private boolean verified;
    private Locations location;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private List<String> favoriteMosques = new ArrayList<>();
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
                this.roles
        );
    }






}