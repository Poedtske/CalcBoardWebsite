package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import com.example.backend.dto.MapSaveRequest;
import com.example.backend.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;


@RestController
@RequestMapping("/maps")
public class MapController {


    @Autowired
    private MapService mapService;

    @PostMapping("/save")
    public ResponseEntity<?> saveMap(@RequestBody MapSaveRequest request) {
        try {
            mapService.saveMap(request);
            return ResponseEntity.ok("Map saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to save map: " + e.getMessage());
        }
    }
}