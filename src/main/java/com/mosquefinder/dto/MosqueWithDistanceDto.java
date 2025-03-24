package com.mosquefinder.dto;

import com.mosquefinder.model.Mosque;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class MosqueWithDistanceDto {
//    private String id;
//    private String name;
//    private String description;
////    private GeoJsonPoint location;
//    private String contactNumber;
//    private Double distance;
//
//    public MosqueWithDistanceDto(String id, String name, String description, GeoJsonPoint location, String contactNumber, Double distance) {
//        this.id = id;
//        this.name = name;
//        this.description = description;
////        this.location = location;
//        this.contactNumber = contactNumber;
//        this.distance = distance;
//    }
    private String id;
    private String name;
    private String description;
    private String contactNumber;
    private Map<String, String> prayerTimes;
    private Double distance;

    public static MosqueWithDistanceDto toDto(Mosque mosque) {

        return MosqueWithDistanceDto.builder()
                .id(mosque.getId())
                .name(mosque.getName())
                .description(mosque.getDescription())
                .contactNumber(mosque.getContactNumber())
                .prayerTimes(mosque.getPrayerTimes())
                .distance(mosque.toDto().getDistance())
                .build();
    }

}
