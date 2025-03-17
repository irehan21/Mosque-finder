package com.mosquefinder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Locations {

    private GeoJsonPoint location;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    // And adjust constructor to match your actual fields
    public Locations(double latitude, double longitude, String address, String city,
                     String state, String country, String postalCode) {
        this.location = new GeoJsonPoint(longitude, latitude);
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
    }

        public Locations toEntity() {
        return new Locations(this.location,this.address,this.city,this.state,this.country,this.postalCode);
    }
}