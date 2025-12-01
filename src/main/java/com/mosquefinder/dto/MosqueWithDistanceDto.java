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

    private String id;
    private String name;
    private String description;
    private String contactNumber;
    private GeoJsonPoint location;
    private Map<String, String> prayerTimes;
    private Double distance;

    public static MosqueWithDistanceDto toDto(Mosque mosque) {

        return MosqueWithDistanceDto.builder()
                .id(mosque.getId())
                .name(mosque.getName())
                .description(mosque.getDescription())
                .contactNumber(mosque.getContactNumber())
                .location(mosque.getLocation())
                .prayerTimes(mosque.getPrayerTimes())
                .distance(mosque.toDto().getDistance())
                .build();
    }

}
