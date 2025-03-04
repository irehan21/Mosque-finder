package com.mosquefinder.service;

import com.mosquefinder.dto.MosqueDto;
import com.mosquefinder.model.Mosque;
import com.mosquefinder.model.Location; 
import com.mosquefinder.repository.MosqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MosqueService {
    private final MosqueRepository mosqueRepository;

    public MosqueDto createMosque(MosqueDto mosqueDto, String createdBy) {
        Mosque mosque = new Mosque();
        mosque.setName(mosqueDto.getName());
        mosque.setDescription(mosqueDto.getDescription());

       
        mosque.setLocation(new Location(
            mosqueDto.getLatitude(),
            mosqueDto.getLongitude()
        ));

        mosque.setContactNumber(mosqueDto.getContactNumber());
        mosque.setPrayerTimes(mosqueDto.getPrayerTimes());
        mosque.setCreatedBy(createdBy);
        mosque.setCreatedAt(LocalDateTime.now());
        mosque.setUpdatedAt(LocalDateTime.now());

        Mosque savedMosque = mosqueRepository.save(mosque);
        return convertToDto(savedMosque);
    }

      public List<MosqueDto> getAllMosques() {
        List<Mosque> mosques = mosqueRepository.findAll();
        return mosques.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ✅ Get Mosque by ID
    public MosqueDto getMosqueById(String id) {
        Optional<Mosque> mosque = mosqueRepository.findById(id);
        return mosque.map(this::convertToDto)
                     .orElseThrow(() -> new RuntimeException("Mosque not found with id: " + id));
    }

    private MosqueDto convertToDto(Mosque mosque) {
        return MosqueDto.builder()
                .id(mosque.getId())
                .name(mosque.getName())
                .description(mosque.getDescription())

                // ✅ Properly extracting Location details
                .latitude(mosque.getLocation() != null ? mosque.getLocation().getLatitude() : 0.0)
                .longitude(mosque.getLocation() != null ? mosque.getLocation().getLongitude() : 0.0)
                .contactNumber(mosque.getContactNumber())
                .prayerTimes(mosque.getPrayerTimes())
                .build();
    }
}
