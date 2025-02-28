package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import com.example.backend.dto.MapSaveRequest;
import com.example.backend.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.backend.model.CalcBoardMap;
import com.example.backend.model.User;
import com.example.backend.service.MapService;
import com.example.backend.config.JwtService;
import com.example.backend.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;



@RestController
@RequestMapping("/maps")
public class MapController {


    private static final Logger logger = LoggerFactory.getLogger(MapController.class);


    @Autowired
    private MapService mapService;

    @Autowired
    private JwtService jwtService;  // Added JwtService

    @Autowired
    private UserRepository userRepository;  // Added UserRepository

    @PostMapping("/save")
    public ResponseEntity<?> saveMap(@RequestBody MapSaveRequest request, @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        String jwtToken = authHeader.replace("Bearer ", "").trim();


        String email = jwtService.extractUsername(jwtToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        request.setUserId(user.getId());
        mapService.saveMap(request);

        return ResponseEntity.ok("Map saved successfully");
    }
}