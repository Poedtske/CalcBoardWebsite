package com.example.backend.config;

import com.example.backend.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * SecurityConfig class handles the configuration of security settings for the application,
 * including HTTP security, CORS, and authentication filters.
 * This class defines the security configurations for both public and secured endpoints,
 * using JWT-based authentication and API key authentication for specific routes.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * AuthenticationProvider instance used to configure how authentication is handled.
     */
    private final AuthenticationProvider authenticationProvider;

    /**
     * JWT authentication filter that validates JWT tokens for incoming requests.
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configures security filter chain for most endpoints, including JWT authentication,
     * role-based access control, and CORS settings.
     *
     * @param http the HttpSecurity instance used to configure the security settings.
     * @return the configured SecurityFilterChain instance.
     * @throws Exception if there is an error during configuration.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection for stateless applications.
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Apply CORS policy.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless sessions.
                .authenticationProvider(authenticationProvider) // Use the provided authentication provider.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT authentication filter.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/avmaps" ,"/api/public/**", "/public/**",
                                "/register","/login","/validate-token",
                                "/static/**" , "/images/**", "/**.jpg" ,"/style-maps.css",
                                "/style.css", "/styles-Home.css" ,"script.js","/maps/**" ).permitAll() // Public access endpoints.
                        .requestMatchers("/api/**",  "/api/secure/**").authenticated()
                        .requestMatchers("/maps/save","/maps", "/maps/download/**").authenticated()// Require authentication for these paths.
                )
                .httpBasic(httpBasic -> httpBasic.disable()) // Disable basic HTTP authentication.
                .logout(logout -> logout.permitAll()); // Allow all users to log out.

        return http.build();
    }



    /**
     * Configures the CORS settings for the application.
     * This method defines the allowed origins, methods, and headers for cross-origin requests.
     *
     * @return a CorsConfigurationSource instance that defines the CORS configuration.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of("http://localhost:8081", "http://localhost:8080", "http://10.2.160.151:8081")); // Allow requests from this origin.
        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allow these HTTP methods.
        corsConfig.setAllowCredentials(true); // Allow sending credentials (cookies).
        corsConfig.addAllowedHeader("*"); // Allow all headers.
        corsConfig.setMaxAge(3600L); // Cache preflight requests for 1 hour.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig); // Apply this CORS configuration to all routes.

        return source;
    }
}
