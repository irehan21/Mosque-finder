package com.mosquefinder.dto;

import com.mosquefinder.model.Locations;
import com.mosquefinder.model.Mosque;
import com.mosquefinder.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MosqueDto {
    private String id;
    private String name;
    private String description;
    private Locations location;
    private String contactNumber;
    private Map<String, String> prayerTimes;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public Mosque toEntity() {
        return new Mosque(
                this.id,
                this.name,
                this.description,
                this.location,
                this.contactNumber,
                this.prayerTimes,
                this.createdBy,
                this.createdAt,
                this.updatedAt,
                this.updatedBy
        );
    }

    public static MosqueDto fromEntity(Mosque mosque) {
        return MosqueDto.builder()
                .id(mosque.getId())
                .name(mosque.getName())
                .description(mosque.getDescription())
                .location(mosque.getLocation())  // ✅ Ensuring Location is included
                .contactNumber(mosque.getContactNumber())
                .prayerTimes(mosque.getPrayerTimes())
                .createdBy(mosque.getCreatedBy())
                .createdAt(mosque.getCreatedAt())
                .updatedAt(mosque.getUpdatedAt())
                .build();
    }


}
