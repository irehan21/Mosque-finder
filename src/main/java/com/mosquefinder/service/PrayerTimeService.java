package com.mosquefinder.service;

import com.batoulapps.adhan.*;
import com.batoulapps.adhan.data.*;
import com.mosquefinder.model.PrayerTimesResponse;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Service
public class PrayerTimeService {

    public PrayerTimesResponse calculatePrayerTimes(double latitude, double longitude) {
        // Set coordinates
        Coordinates coordinates = new Coordinates(latitude, longitude);

        // Get today's date
        DateComponents date = DateComponents.from(new Date());

        // Configure calculation parameters for Indian subcontinent
        CalculationParameters params = CalculationMethod.KARACHI.getParameters();

        // Set Hanafi madhab
        params.madhab = Madhab.HANAFI;

        // Calculate prayer times
        PrayerTimes prayerTimes = new PrayerTimes(coordinates, date, params);

        // Use Indian timezone
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        // Create response object

        return new PrayerTimesResponse(
                formatter.format(prayerTimes.fajr),
                formatter.format(prayerTimes.sunrise),
                formatter.format(prayerTimes.dhuhr),
                formatter.format(prayerTimes.asr),
                formatter.format(prayerTimes.maghrib),
                formatter.format(prayerTimes.isha)
        );
    }
}
