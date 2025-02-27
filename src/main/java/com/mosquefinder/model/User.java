package com.mosquefinder.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class User {
    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String mobileNumber;

    private String email;

    private String passwordHash;

    private GeoJsonPoint lastLocation;

    private List<String> favoriteMosqueIds = new ArrayList<>();

    private Date createdAt = new Date();

    private Date lastLoginAt;

    private String role = "USER"; // Default role

}







