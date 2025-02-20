package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.service.AuthService;
import com.example.backend.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/secure")
public class UserController {

    private final UserServiceImpl userServiceImpl;
    private final AuthService service;

    /**
     * Endpoint for retrieving the profile of the currently authenticated user.
     * <p>
     * This route is used to fetch the profile details of the user who is currently logged in.
     * The user is identified by their authentication token, and their details are returned
     * in the response.
     * </p>
     *
     * @return {@link ResponseEntity} containing the user's profile data if successful.
     */
    @GetMapping("/profile")
    public ResponseEntity getProfile() {
        return userServiceImpl.getUserProfile(getUsername()); // Use the service to fetch user details
    }

    /**
     * Endpoint for etrieving the current user's shopping cart.
     * <p>
     * This route returns the items present in the authenticated user's shopping cart.
     * The items are retrieved based on the user's authentication and are returned
     * in the response.
     * </p>
     *
     * @return {@link ResponseEntity} containing the user's cart details if successful.
     */
    @GetMapping("/cart")
    public ResponseEntity getCart(){
        return userServiceImpl.getCart(getUsername()); // Use the service to fetch user details
    }

    /**
     *  Endpoint for clearing all items from the authenticated user's shopping cart.
     * <p>
     * This route allows the authenticated user to clear their entire shopping cart.
     * Once executed, the cart will be empty for that user.
     * </p>
     *
     * @return {@link ResponseEntity} indicating the result of the operation.
     */
    @DeleteMapping("/cart")
    public ResponseEntity clearCart(){
        return userServiceImpl.clearCart(getUsername()); // Use the service to fetch user details
    }

    /**
     * Endpoint for removing a specific ticket from the authenticated user's shopping cart.
     * <p>
     * This route allows the user to remove a ticket from their cart based on its ID.
     * The removal operation is performed for the authenticated user.
     * </p>
     *
     * @param id {@link Long} representing the ticket ID to be removed from the cart.
     * @return {@link ResponseEntity} indicating the result of the operation.
     */
    @DeleteMapping("/cart/tickets/{id}")
    public ResponseEntity removeTicketFromCart(@PathVariable Long id){
        return userServiceImpl.removeTicket(getUsername(), id); // Use the service to fetch user details
    }

    /**
     * Endpoint for removing a specific product from the authenticated user's shopping cart.
     * <p>
     * This route allows the user to remove a product from their cart based on its ID.
     * The removal operation is performed for the authenticated user.
     * </p>
     *
     * @param id {@link Long} representing the product ID to be removed from the cart.
     * @return {@link ResponseEntity} indicating the result of the operation.
     */
    @DeleteMapping("/cart/products/{id}")
    public ResponseEntity removeProductFromCart(@PathVariable Long id){
        return userServiceImpl.removeProduct(getUsername(), id); // Use the service to fetch user details
    }

    /**
     * Endpoint for processing the payment for the authenticated user's shopping cart.
     * <p>
     * This route allows the authenticated user to pay for the items in their cart.
     * The payment data is provided in the request body and includes the necessary
     * details for completing the transaction.
     * </p>
     *
     * @param paymentData {@link List<ProductPaymentDTO>} containing the payment information for the products.
     * @return {@link ResponseEntity} indicating the result of the payment operation.
     */
    @PutMapping("pay")
    public ResponseEntity payCart(@RequestBody List<ProductPaymentDTO> paymentData){
        return  userServiceImpl.pay(getUsername(), paymentData);
    }

    /**
     * Helper method to retrieve the username of the currently authenticated user.
     * <p>
     * This method fetches the logged-in user's username from the security context.
     * It is used internally to access the authenticated user's identity across
     * various routes in this controller.
     * </p>
     *
     * @return {@link String} representing the username of the authenticated user.
     */
    private String getUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

}
