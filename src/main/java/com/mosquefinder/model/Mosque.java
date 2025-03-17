package com.mosquefinder.model;

import com.mosquefinder.dto.MosqueDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "mosques")
public class Mosque {
    @Id
    private String id;
    private String name;
    private String description;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;
    private String contactNumber;
    private Map<String, String> prayerTimes;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public MosqueDto toDto() {
        return new MosqueDto(
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


}