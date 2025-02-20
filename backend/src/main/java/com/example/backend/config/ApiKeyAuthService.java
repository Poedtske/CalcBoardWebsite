package com.example.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * API Key Authentication Service.
 * <p>
 * This service is responsible for handling API key-based authentication.
 * It verifies the provided API key from the request header and, if valid,
 * returns an authenticated token. Otherwise, it throws a {@link BadCredentialsException}.
 * </p>
 *
 * Usage:
 * The service expects the API key to be provided in the HTTP request header:
 * <strong>X-API-KEY</strong>.
 */
public class ApiKeyAuthService {

    /**
     * The name of the HTTP header that should contain the API key.
     */
    private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";

    /**
     * The predefined valid API key used for authentication.
     */
    private static final String AUTH_TOKEN = "helloThere";


    /**
     * Authenticates the request by validating the API key provided in the header.
     * <p>
     * If the API key is missing or invalid, this method throws a {@link BadCredentialsException}.
     * If valid, it returns an instance of {@link ApiKeyAuthentication}.
     * </p>
     *
     * @param request {@link HttpServletRequest} The incoming HTTP request.
     * @return {@link Authentication} An authenticated token with no granted authorities.
     * @throws BadCredentialsException If the API key is invalid or missing.
     */
    public static Authentication getAuthentication(HttpServletRequest request) {
        // Extract the API key from the request header
        String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);

        // Validate the API key
        if (apiKey == null || !apiKey.equals(AUTH_TOKEN)) {
            throw new BadCredentialsException("Invalid API Key"); // Reject invalid or missing API keys
        }

        // Return an authenticated token with no additional authorities
        return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
    }
}

