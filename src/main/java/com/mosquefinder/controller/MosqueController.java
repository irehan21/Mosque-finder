package com.mosquefinder.controller;

import com.mosquefinder.dto.MosqueDto;
import com.mosquefinder.dto.MosqueWithDistanceDto;
import com.mosquefinder.exception.ResourceNotFoundException;
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
        MosqueDto createdMosque = mosqueService.createMosque(mosqueDto, createdBy).toDto();

        return ResponseEntity.ok()
                .header("success", "Mosque created successfully")
                .body(createdMosque);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<MosqueDto> updateMosque(@PathVariable String id, @RequestBody  MosqueDto mosqueDto, Authentication authentication) {
        String updatedBy = authentication.getName();
        mosqueService.updateMosque(id,mosqueDto,authentication,updatedBy).toDto();
        return ResponseEntity.ok().header("success", "Mosque Update Successful").body(mosqueDto);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<MosqueDto>> getAllMosque() {
        return ResponseEntity.ok(mosqueService.getAllMosques());
    }

//    @GetMapping("getById/{id}")
//    public ResponseEntity<MosqueDto> getMosqueById(@PathVariable String id) {
//        return ResponseEntity.ok(mosqueService.getMosqueById(id));
//    }

    @GetMapping("getById/{id}")
    public ResponseEntity<MosqueDto> getMosqueById(@PathVariable String id) {
        try {
            MosqueDto mosqueDto = mosqueService.getMosqueById(id);

            return ResponseEntity.ok(mosqueDto);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/getCreatedBy")
    public ResponseEntity<MosqueDto> getCreatedByMosque( Authentication authentication) {
       MosqueDto mosqueDto= mosqueService.getCreatedBy(authentication);
        return ResponseEntity.ok(mosqueDto);
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
            @RequestParam(defaultValue = "2000") double maxDistance
    ) {
        return mosqueService.findMosquesNear(latitude, longitude, maxDistance);
    }

}
