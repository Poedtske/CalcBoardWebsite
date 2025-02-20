package com.example.backend.service;

import com.example.backend.config.JwtService;
import com.example.backend.controller.AuthenticationResponse;
import com.example.backend.dto.CredentialsDto;
import com.example.backend.dto.SignUpDto;
import com.example.backend.enums.Role;
import com.example.backend.model.Invoice;
import com.example.backend.model.User;
import com.example.backend.repository.InvoiceRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * AuthService class provides authentication-related services, including user registration, login, and JWT token generation.
 * It interacts with the User and Invoice repositories, encrypts user passwords, and generates JWT tokens for authenticated users.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    /**
     * PasswordEncoder used to encrypt user passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * UserRepository used to interact with the User entity in the database.
     */
    private final UserRepository userRepository;

    /**
     * JwtService used to generate and validate JWT tokens.
     */
    private final JwtService jwtService;

    /**
     * AuthenticationManager used for managing user authentication.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * InvoiceRepository used to interact with the Invoice entity in the database.
     */
    private final InvoiceRepository invoiceRepository;

    /**
     * Registers a new user in the system by saving their details to the database, creating an associated invoice,
     * and generating a JWT token for the newly registered user.
     *
     * @param signUpDto the Data Transfer Object (DTO) containing the user's sign-up information.
     * @return an AuthenticationResponse containing the generated JWT token.
     */
    public AuthenticationResponse register(SignUpDto signUpDto) {
        // Create a new user from the sign-up details and encode the password
        var user = User.builder()
                .firstName(signUpDto.getFirstName())
                .lastName(signUpDto.getLastName())
                .email(signUpDto.getEmail())
                .role(Role.USER)
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .build();
        // Save the user to the database
        userRepository.save(user);

        // Create an invoice for the user
        Invoice i = new Invoice(user);
        invoiceRepository.save(i);

        // Link the invoice to the user
        user.addInvoice(i);
        userRepository.save(user);

        // Generate a JWT token with the user's role
        Map<String, Object> role = new HashMap<>();
        role.put("role", user.getRole());
        var jwtToken = jwtService.generateToken(role, user);

        // Return the authentication response with the JWT token
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Authenticates a user based on their credentials, generates a JWT token if authentication is successful,
     * and returns the token in an AuthenticationResponse.
     *
     * @param credentialsDto the Data Transfer Object (DTO) containing the user's login credentials (email and password).
     * @return an AuthenticationResponse containing the generated JWT token.
     * @throws AuthenticationException if authentication fails.
     */
    public AuthenticationResponse login(CredentialsDto credentialsDto) {
        // Authenticate the user using the provided credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentialsDto.getEmail(),
                        credentialsDto.getPassword()));

        // Retrieve the authenticated user from the database
        var user = userRepository.findByEmail(credentialsDto.getEmail()).orElseThrow();

        // Generate a JWT token with the user's roles/authorities
        Map<String, Object> role = new HashMap<>();
        role.put("role", user.getAuthorities());
        var jwtToken = jwtService.generateToken(role, user);

        // Return the authentication response with the JWT token
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
