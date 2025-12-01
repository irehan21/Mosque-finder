package com.mosquefinder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrayerTimesResponse {
    private String fajr;
    private String dhuhar;
    private String asr;
    private String maghrib;
    private String isha;
    private String juma;


}