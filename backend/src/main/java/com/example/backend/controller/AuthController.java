package com.example.backend.controller;

import com.example.backend.dto.CredentialsDto;
import com.example.backend.dto.SignUpDto;
import com.example.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for authentication operations, such as user login and registration.
 * <p>
 * This controller exposes endpoints for user authentication and registration,
 * handling requests related to login and user registration processes.
 * </p>
 */
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    /**
     * Endpoint for authenticating a user based on their provided credentials.
     * <p>
     * This route accepts the user credentials (username and password), verifies them,
     * and returns an authentication token if the credentials are valid. The response includes
     * an authentication response with a token that can be used for subsequent requests.
     * </p>
     *
     * @param credentialsDto {@link CredentialsDto} containing the user's credentials (username and password).
     * @return {@link ResponseEntity} containing the {@link AuthenticationResponse} if login is successful.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody CredentialsDto credentialsDto){
        return ResponseEntity.ok(service.login(credentialsDto)); // Calls the service to authenticate the user
    }

    /**
     * Endpoint for registering a new user in the system.
     * <p>
     * This route accepts user registration details, including username, password, and other necessary information.
     * It creates a new user and returns an authentication response containing a token for the newly registered user.
     * </p>
     *
     * @param signUpDto {@link SignUpDto} containing the user's registration details (username, password, etc.).
     * @return {@link ResponseEntity} containing the {@link AuthenticationResponse} if registration is successful.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register (@RequestBody SignUpDto signUpDto){
        return ResponseEntity.ok(service.register(signUpDto)); // Calls the service to register the new user
    }
}
