package com.example.backend.service.impl;

import com.example.backend.dto.*;
import com.example.backend.exceptions.AppException;
import com.example.backend.mapper.UserMapper;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing user-related operations including cart management, user registration, and payment.
 * This class handles various business operations for users, such as adding products to their cart,
 * viewing the cart, paying for products, and more.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Finds a user by their email and maps it to a UserDto.
     *
     * <p>
     *     <Strong>Warning:</Strong> This method is currently not being used
     * </p>
     *
     * @param email The email of the user to find.
     * @return {@link UserDto} containing user details.
     * @throws AppException If the user with the given email is not found.
     */
    @Override
    public UserDto findUserDtoByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        return userMapper.toUserDto(user);
    }

    /**
     * Retrieves a user by their email without mapping to a DTO.
     *
     * @param email The email of the user to find.
     * @return {@link User} object containing user details.
     * @throws AppException If the user with the given email is not found.
     */
    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
    }


    /**
     * Retrieves the profile of a user based on their email address.
     * <p>
     * This method fetches the user's profile information, such as their email, first name, and last name,
     * and returns it as a {@link UserDto} object. If the user is not found, a {@link RuntimeException} will be thrown.
     * If an error occurs, an appropriate HTTP status code and message will be returned in the response.
     * </p>
     *
     * @param email The email of the user whose profile is to be fetched.
     * @return {@link ResponseEntity} with:
     * <ul>
     *     <li>{@code 200 OK} with the user's profile in the response body if the user is found.</li>
     *     <li>{@code 400 Bad Request} if an application-specific exception occurs (e.g., user not found).</li>
     *     <li>{@code 500 Internal Server Error} if an unexpected error occurs.</li>
     * </ul>
     */
    @Override
    public ResponseEntity getUserProfile(String email) {
        try {
            User user = findUserByEmail(email);
            return ResponseEntity.ok(new UserDto(user.getEmail(), user.getFirstName(), user.getLastName()));
        } catch (AppException a) {
            return ResponseEntity.status(a.getCode()).body(a.getMessage());  // Handles app-specific exception
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e);  // Handles unexpected errors
        }
    }

}
