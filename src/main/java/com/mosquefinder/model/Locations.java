package com.mosquefinder.model;

import com.mosquefinder.dto.LocationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Locations {
    private double latitude;
    private double longitude;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    // Convert Locations to LocationDto
    public LocationDto toDto() {
        return new LocationDto(latitude, longitude, address, city, state, country, postalCode);
    }

    public Locations toEntity() {
        return new Locations(latitude, longitude, address, city, state, country, postalCode);
    }

}