package com.mosquefinder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MosqueDto {
    private String id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private String contactNumber;
    private Map<String, String> prayerTimes;
}
