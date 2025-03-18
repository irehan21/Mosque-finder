package com.mosquefinder.controller;

import com.mosquefinder.dto.MosqueDto;
import com.mosquefinder.dto.MosqueWithDistanceDto;
import com.mosquefinder.service.MosqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mosque/api")
@RequiredArgsConstructor
public class MosqueController {
    private final MosqueService mosqueService;
    private final MongoTemplate mongoTemplate;


    @PostMapping("/create")
    public ResponseEntity<MosqueDto> createMosque(@RequestBody MosqueDto mosqueDto, Authentication authentication) {
        String createdBy = authentication.getName(); // Fetch user from JWT
        return ResponseEntity.ok(mosqueService.createMosque(mosqueDto, createdBy).toDto());
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<MosqueDto> updateMosque(@PathVariable String id, @RequestBody  MosqueDto mosqueDto, Authentication authentication) {
        String updatedBy = authentication.getName();
        return ResponseEntity.ok(mosqueService.updateMosque(id,mosqueDto,authentication,updatedBy).toDto());
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<MosqueDto>> getAllMosque() {
        return ResponseEntity.ok(mosqueService.getAllMosques());
    }

    @GetMapping("getById/{id}")
    public ResponseEntity<MosqueDto> getMosqueById(@PathVariable String id) {
        return ResponseEntity.ok(mosqueService.getMosqueById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMosque(@PathVariable String id, Authentication authentication) {
        mosqueService.deleteMosque(id,authentication);
        return ResponseEntity.ok("Mosque deleted successfully");
    }


    @GetMapping("/nearest")
    public List<MosqueWithDistanceDto> getNearestMosques(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "10") double maxDistance // Default 10km
    ) {
        return mosqueService.findMosquesNear(latitude, longitude, maxDistance);
    }

}
