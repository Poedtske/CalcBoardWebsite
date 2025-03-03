package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.service.AuthService;
import com.example.backend.service.MapService;
import com.example.backend.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/secure")
public class UserController {

    private final UserServiceImpl userServiceImpl;
    private final AuthService service;
    private final MapService mapService;


    /**
     * Endpoint for retrieving the profile of the currently authenticated user.
     * <p>
     * This route is used to fetch the profile details of the user who is currently logged in.
     * The user is identified by their authentication token, and their details are returned
     * in the response.
     * </p>
     *
     * @return {@link ResponseEntity} containing the user's profile data if successful.
     */
    @GetMapping("/profile")
    public ResponseEntity getProfile() {
        return userServiceImpl.getUserProfile(getUsername()); // Use the service to fetch user details
    }


    /**
     * Helper method to retrieve the username of the currently authenticated user.
     * <p>
     * This method fetches the logged-in user's username from the security context.
     * It is used internally to access the authenticated user's identity across
     * various routes in this controller.
     * </p>
     *
     * @return {@link String} representing the username of the authenticated user.
     */
    private String getUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }


    @PutMapping("/{mapId}/toggle-privacy")
    public ResponseEntity<String> togglePrivacy(@PathVariable Integer mapId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        mapService.toggleMapPrivacy(mapId, username);
        return ResponseEntity.ok("Privacy updated successfully");
    }

}
