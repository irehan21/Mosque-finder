package com.mosquefinder.service;

import com.mosquefinder.dto.MosqueDto;
import com.mosquefinder.model.Mosque;
import com.mosquefinder.repository.MosqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MosqueService {
    private final MosqueRepository mosqueRepository;

    public List<MosqueDto> getAllMosques() {
        List<Mosque> mosques = mosqueRepository.findAll();
        return mosques.stream().map(MosqueDto::fromEntity).collect(Collectors.toList());
    }

    public Mosque createMosque(MosqueDto mosqueDto, String createdBy) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user has ADMIN role
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

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
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

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
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new RuntimeException("Unauthorized! Only ADMIN can delete mosques.");
        }

        mosqueRepository.findById(mosqueId)
                .orElseThrow(() -> new RuntimeException("Mosque not found with id: " + mosqueId));

        mosqueRepository.deleteById(mosqueId);

    }

}
