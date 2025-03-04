package com.mosquefinder.dto;

import com.mosquefinder.model.Locations;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    private double latitude;
    private double longitude;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    public Locations toEntity() {
        return new Locations(latitude, longitude, address, city, state, country, postalCode);
    }
}
