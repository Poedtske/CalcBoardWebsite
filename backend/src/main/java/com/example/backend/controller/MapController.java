package com.example.backend.controller;

import com.example.backend.dto.MapSaveRequest;
import com.example.backend.model.User;
import com.example.backend.service.MapService;
import com.example.backend.config.JwtService;
import com.example.backend.repository.UserRepository;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // For file upload
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



@RestController
@RequestMapping("/maps")
public class MapController {


    private static final Logger logger = LoggerFactory.getLogger(MapController.class);


    @Autowired
    private MapService mapService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    // This endpoint handles both the map JSON data and the uploaded image file
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveMap(
            @RequestPart(value = "mapData") MapSaveRequest mapSaveRequest,  // JSON data sent as part of the multipart request
            @RequestPart(value = "tileImages", required = false) MultipartFile[] tileImages, // Tile images (as an array)
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (tileImages == null) {
            return ResponseEntity.badRequest().body("Error: No images received!");
        }

        // Check if Authorization header exists
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        // Extract username from JWT token
        String jwtToken = authHeader.replace("Bearer ", "").trim();
        String email = jwtService.extractUsername(jwtToken);

        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        try {
            // Set user ID in the map save request
            mapSaveRequest.setId(user.getId());

            System.out.println(tileImages.length);
            System.out.println("Received MapSaveRequest: " + mapSaveRequest);
            System.out.println("Received map data: " + mapSaveRequest.toString());

            // Call the service to save the map and pass tile images
            mapService.saveMap(mapSaveRequest, tileImages);

            return ResponseEntity.ok("Map and tile images saved successfully");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request: " + e.getMessage());
        }
    }

}