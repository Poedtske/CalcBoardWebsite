package com.example.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * API Key Authentication Filter.
 * <p>
 * This filter is responsible for securing specific API endpoints using API key authentication.
 * It checks the incoming requests to determine if they are targeting secure endpoints and
 * verifies their authentication using the {@link ApiKeyAuthService}.
 * </p>
 *
 * <p>
 * Requests that fail authentication will receive an HTTP 401 Unauthorized response.
 * Successful authentication allows the request to proceed further down the filter chain.
 * </p>
 *
 * Usage:
 * This filter is intended to be used for endpoints that require API key validation,
 * specifically paths starting with <strong>/api/secure/spondEvents/</strong>.
 */
public class ApiKeyAuthFilter extends GenericFilterBean {

    /**
     * Processes each incoming request to enforce API key authentication.
     * <p>
     * If the request targets a secure endpoint, the filter validates the provided API key.
     * Unauthorized requests receive a 401 response, while valid requests proceed through the filter chain.
     * </p>
     *
     * @param request      {@link ServletRequest} Incoming request object.
     * @param response     {@link ServletResponse} Response object for sending results.
     * @param filterChain  {@link FilterChain} Allows the request to proceed through the filter pipeline.
     * @throws IOException      If an input or output error occurs.
     * @throws ServletException If the processing of the request fails.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Retrieve the request path for evaluation
        String requestPath = httpRequest.getRequestURI();

        try {
            // Perform authentication for secure endpoints only
            if (isSecureEndpoint(requestPath)) {
                Authentication authentication = ApiKeyAuthService.getAuthentication(httpRequest);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception exp) {
            // Handle authentication failure by sending a 401 Unauthorized response
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

            PrintWriter writer = httpResponse.getWriter();
            writer.print(exp.getMessage());
            writer.flush();
            writer.close();

            // Stop processing further filters for unauthorized requests
            return;
        }

        // Allow the request to proceed through the remaining filters
        filterChain.doFilter(request, response);
    }

    /**
     * Determines if the request targets a secure endpoint requiring authentication.
     * <p>
     * This method checks if the request path starts with the secure API prefix: <strong>/api/secure/spondEvents/</strong>.
     * </p>
     *
     * @param requestPath {@link String} The URI of the incoming request.
     * @return {@code true} if the endpoint requires authentication, {@code false} otherwise.
     */
    private boolean isSecureEndpoint(String requestPath) {
        return requestPath.startsWith("/api/secure/spondEvents/");
    }
}

