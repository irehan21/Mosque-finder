package com.mosquefinder.service;

import com.mosquefinder.dto.MosqueDto;
import com.mosquefinder.dto.MosqueWithDistanceDto;
import com.mosquefinder.model.Mosque;
import com.mosquefinder.repository.MosqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GeoNearOperation;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MosqueService {
    private final MosqueRepository mosqueRepository;
    private final MongoTemplate mongoTemplate;

    public List<MosqueDto> getAllMosques() {
        List<Mosque> mosques = mosqueRepository.findAll();
        return mosques.stream().map(MosqueDto::fromEntity).collect(Collectors.toList());
    }

    public Mosque createMosque(MosqueDto mosqueDto, String createdBy) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user has ADMIN role
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("MOSQUE_ADMIN"));

        if (!isAdmin) {
            throw new RuntimeException("Unauthorized! Only ADMIN can create or edit mosques.");
        }  else  {
            // Creating new mosque
            Mosque newMosque = Mosque.builder()
                    .name(mosqueDto.getName())
                    .description(mosqueDto.getDescription())
                    .location(mosqueDto.getLocation())
                    .contactNumber(mosqueDto.getContactNumber())
                    .prayerTimes(mosqueDto.getPrayerTimes())
                    .createdBy(createdBy)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            return mosqueRepository.save(newMosque);
        }

    }

    public Mosque updateMosque(String id, MosqueDto mosqueDto,  Authentication authentication , String updatedBy) {
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("MOSQUE_ADMIN"));

        if (!isAdmin) {
            throw new RuntimeException("Unauthorized! Only ADMIN can update mosques.");
        }

        // Check if the mosque exists
        Mosque mosque = mosqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mosque not found with ID: " + id));

        // Update mosque details
        mosque.setName(mosqueDto.getName());
        mosque.setDescription(mosqueDto.getDescription());
        mosque.setLocation(mosqueDto.getLocation());
        mosque.setContactNumber(mosqueDto.getContactNumber());
        mosque.setPrayerTimes(mosqueDto.getPrayerTimes());
        mosque.setUpdatedAt(LocalDateTime.now());
        mosque.setUpdatedBy(updatedBy);

        return mosqueRepository.save(mosque);

    }

    public MosqueDto getMosqueById(String id) {
        Mosque mosque = mosqueRepository.findById(id).orElseThrow(() -> new RuntimeException("Mosque not found with ID: " + id));
        return MosqueDto.fromEntity(mosque);
    }


    public void deleteMosque(String mosqueId, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("MOSQUE_ADMIN"));

        if (!isAdmin) {
            throw new RuntimeException("Unauthorized! Only ADMIN can delete mosques.");
        }

        mosqueRepository.findById(mosqueId)
                .orElseThrow(() -> new RuntimeException("Mosque not found with id: " + mosqueId));

        mosqueRepository.deleteById(mosqueId);

    }

    public List<MosqueWithDistanceDto> findMosquesNear(double latitude, double longitude, double distanceInKilometers) {
        Point point = new Point(longitude, latitude);
        Distance distance = new Distance(distanceInKilometers, Metrics.KILOMETERS);

        NearQuery nearQuery = NearQuery.near(point)
                .maxDistance(distance)
                .in(Metrics.KILOMETERS)
                .spherical(true);

        // GeoNear aggregation to calculate distance
        GeoNearOperation geoNearOperation = Aggregation.geoNear(nearQuery, "distance");

        // Build aggregation pipeline
        Aggregation aggregation = Aggregation.newAggregation(
                geoNearOperation,
                Aggregation.project("id", "name", "description","location", "contactNumber", "prayerTimes")
                        .and("distance").as("distance"),
                Aggregation.sort(org.springframework.data.domain.Sort.Direction.ASC, "distance") // Sort nearest first
        );

        // Execute aggregation and map results
        AggregationResults<MosqueWithDistanceDto> results = mongoTemplate.aggregate(aggregation, "mosques", MosqueWithDistanceDto.class);
        return results.getMappedResults();
    }

    public MosqueDto getCreatedBy(Authentication authentication) {
        String createdBy = authentication.getName();

        return mosqueRepository.findByCreatedBy(createdBy)
                .map(mosque -> MosqueDto.fromEntity((Mosque) mosque))
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Mosque not found for user: " + createdBy)
                );
    }


}


