package com.mosquefinder.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

@Data
@NoArgsConstructor

public class MosqueWithDistanceDto {
    private String id;
    private String name;
    private String description;
    private GeoJsonPoint location;
    private String contactNumber;
    private Double distance;

    public MosqueWithDistanceDto(String id, String name, String description, GeoJsonPoint location, String contactNumber, Double distance) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.contactNumber = contactNumber;
        this.distance = distance;
    }

}
