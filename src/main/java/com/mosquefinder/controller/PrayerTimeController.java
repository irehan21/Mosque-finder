package com.mosquefinder.controller;

import com.mosquefinder.model.PrayerTimesResponse;
import com.mosquefinder.service.PrayerTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prayer-times")
public class PrayerTimeController {

    private final PrayerTimeService prayerTimeService;

    @Autowired
    public PrayerTimeController(PrayerTimeService prayerTimeService) {
        this.prayerTimeService = prayerTimeService;
    }

    @GetMapping
    public ResponseEntity<PrayerTimesResponse> getPrayerTimes(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude) {

        PrayerTimesResponse prayerTimesResponse = prayerTimeService.calculatePrayerTimes(latitude, longitude);
        return ResponseEntity.ok(prayerTimesResponse);
    }
}