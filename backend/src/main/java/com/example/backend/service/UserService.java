package com.example.backend.service;


import com.example.backend.dto.UserDto;
import com.example.backend.model.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {

    UserDto findUserDtoByEmail(String email);

    User findUserByEmail(String email);

    ResponseEntity getUserProfile(String email);


}
