package com.mosquefinder.controller;

import com.mosquefinder.dto.MosqueDto;
import com.mosquefinder.service.MosqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mosques")
@RequiredArgsConstructor
public class MosqueController {
    private final MosqueService mosqueService;

    @GetMapping
    public ResponseEntity<List<MosqueDto>> getAllMosques() {
        return ResponseEntity.ok(mosqueService.getAllMosques());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MosqueDto> getMosqueById(@PathVariable String id) {
        return ResponseEntity.ok(mosqueService.getMosqueById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<MosqueDto> createMosque(@RequestBody MosqueDto mosqueDto, Authentication authentication) {
        String createdBy = authentication.getName(); // JWT se user ka naam lega
        return ResponseEntity.ok(mosqueService.createMosque(mosqueDto, createdBy));
    }
    
}
