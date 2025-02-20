package com.example.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 * <p>
 * This filter intercepts HTTP requests to validate JWT tokens and authenticate users.
 * It ensures that only authorized users can access secured resources.
 * </p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *     <li>Extracts JWT token from the Authorization header.</li>
 *     <li>Validates the token and extracts user details.</li>
 *     <li>Sets the authentication context if the token is valid.</li>
 * </ul>
 *
 * <p><strong>Dependencies:</strong></p>
 * <ul>
 *     <li>{@link JwtService} - Handles JWT operations (token generation, validation, extraction).</li>
 *     <li>{@link UserDetailsService} - Loads user details for authentication.</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Service for handling JWT operations.
     */
    private final JwtService jwtService;

    /**
     * Service for loading user details.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Filters incoming requests to check for JWT authentication.
     *
     * @param request     Incoming HTTP request.
     * @param response    HTTP response.
     * @param filterChain Filter chain for processing the request.
     * @throws ServletException If an error occurs during filtering.
     * @throws IOException      If an I/O error occurs during filtering.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Retrieve Authorization header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Validate the header and check for "Bearer " prefix
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the JWT token from the header
        jwt = authHeader.substring(7);

        // Extract the email (username) from the JWT token
        userEmail = jwtService.extractUsername(jwt);

        // Check if email exists and no authentication token is already set
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user details using the extracted email
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Validate the JWT token and set authentication
            if (jwtService.isTokenValid(jwt, userDetails)) { // Fixes previous typo 'isTokenValis'

                // Create an authentication token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Set additional authentication details
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Update the Security Context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}

