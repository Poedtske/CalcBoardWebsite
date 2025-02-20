package com.example.backend.service;

import com.example.backend.dto.ProductDto;
import com.example.backend.dto.ProductPaymentDTO;
import com.example.backend.dto.UserDto;
import com.example.backend.model.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {

    UserDto findUserDtoByEmail(String email);

    User findUserByEmail(String email);

    ResponseEntity addProductToUserCart(String email, ProductDto productDto);

    ResponseEntity getUserProfile(String email);

    ResponseEntity<?> getCart(String email);

    ResponseEntity clearCart(String email);

    ResponseEntity removeTicket(String email, Long id);

    ResponseEntity removeProduct(String email, Long id);

    public void removeUnavailableProductsFromCart(User u);

    ResponseEntity pay(String email, List<ProductPaymentDTO> productPayments);
}
