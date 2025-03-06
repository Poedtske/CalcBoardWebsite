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
            @RequestPart(value = "file", required = false) MultipartFile file,  // Image file sent as part of the multipart request
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

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
            mapSaveRequest.setUserId(user.getId());

            // Get the storage path for the image (from environment variable)
            String storagePath = System.getenv("MAP_STORAGE_PATH");
            if (storagePath == null || storagePath.isEmpty()) {
                storagePath = "/app/maps";  // Default to the volume path
            }

            // Process and save the image if it's provided
            if (file != null && !file.isEmpty()) {
                String fileName = "map_" + System.currentTimeMillis() + ".png";
                Path filePath = Paths.get(storagePath, fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, file.getBytes());

                // Store the image filename in the map save request
                mapSaveRequest.getMap().setImg(fileName);
            }

            // Call the service to save the map (including the JSON data and image filename)
            mapService.saveMap(mapSaveRequest, file);  // Pass both the mapSaveRequest and the file to the service

            return ResponseEntity.ok("Map and image saved successfully");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request: " + e.getMessage());
        }
     }


   /* @GetMapping("/maps")
    public String usersMap(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        List<CalcBoardMap> userMaps = mapService.getUserMaps(username);
        model.addAttribute("maps", userMaps);

        return "UsersMap"; // UsersMap.html will display the maps
    }*/
}