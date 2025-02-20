package com.example.backend.config;

import com.example.backend.repository.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * Application Security Configuration.
 * <p>
 * This class configures the authentication and password encoding mechanisms for the application.
 * It integrates with the database to fetch user details and validates user credentials.
 * </p>
 *
 * <p><strong>Beans provided:</strong></p>
 * <ul>
 *     <li>{@link UserDetailsService} - Loads user details from the database based on the provided username.</li>
 *     <li>{@link AuthenticationProvider} - Handles authentication logic using DAO-based authentication.</li>
 *     <li>{@link PasswordEncoder} - Provides password hashing and validation using BCrypt.</li>
 *     <li>{@link AuthenticationManager} - Delegates authentication requests to configured providers.</li>
 * </ul>
 *
 * Dependencies:
 * - {@link UserRepository}: Used to fetch user information from the database.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    /**
     * Repository for fetching user information.
     */
    private final UserRepository userRepository;

    /**
     * Configures the {@link UserDetailsService} bean.
     * <p>
     * This service retrieves user details based on the email provided during authentication.
     * Throws {@link UsernameNotFoundException} if no matching user is found.
     * </p>
     *
     * @return {@link UserDetailsService} implementation for database lookup.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Configures the {@link AuthenticationProvider} bean.
     * <p>
     * Uses {@link DaoAuthenticationProvider} to authenticate users with the provided
     * {@link UserDetailsService} and {@link PasswordEncoder}.
     * </p>
     *
     * @return Configured {@link AuthenticationProvider}.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Configures the {@link PasswordEncoder} bean.
     * <p>
     * Uses BCrypt hashing algorithm for secure password storage and validation.
     * </p>
     *
     * @return {@link BCryptPasswordEncoder} instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the {@link AuthenticationManager} bean.
     * <p>
     * Delegates authentication requests to the configured {@link AuthenticationProvider}.
     * </p>
     *
     * @param authenticationConfiguration Configuration for authentication.
     * @return Configured {@link AuthenticationManager}.
     * @throws Exception If an error occurs while building the manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}

