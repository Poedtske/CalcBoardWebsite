package com.example.backend.service.impl;

import com.example.backend.dto.*;
import com.example.backend.exceptions.AppException;
import com.example.backend.mapper.UserMapper;
import com.example.backend.model.Invoice;
import com.example.backend.model.Product;
import com.example.backend.model.User;
import com.example.backend.repository.InvoiceRepository;
import com.example.backend.repository.ProductRepository;
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
    private final ProductServiceImpl productService;
    private final UserMapper userMapper;
    private final TicketServiceImpl ticketService;
    private final InvoiceRepository invoiceRepository;

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
     * Adds a product to a user's shopping cart.
     * <p>
     * This method adds a specified product to the user's active invoice (cart). The product details, including product ID
     * and quantity, are provided via the {@link ProductDto} object. Before adding the product to the cart, it checks whether
     * the product is available. If the product is no longer available, it returns an error message.
     * </p>
     *
     * <p>
     * <strong>Warning:</strong> This method is currently outdated and may not be in active use.
     * This is because currently, 24/12/2024, the products are managed in the sessiondata in the frontend.
     * Products are only placed in the invoice when the transaction is being completed. The effect of which leads
     *  to the method no longer being used.
     * </p>
     *
     * @param email The email of the user to whom the product is to be added.
     * @param productDto The data transfer object containing the product details (ID and quantity) to be added to the cart.
     * @return {@link ResponseEntity} indicating the result of the operation:
     * <ul>
     *     <li>{@code 200 OK} if the product is successfully added to the user's cart.</li>
     *     <li>{@code 400 Bad Request} if the product is no longer available or an application-specific exception occurs.</li>
     *     <li>{@code 500 Internal Server Error} if an unexpected error occurs during the process.</li>
     * </ul>
     */
    @Override
    public ResponseEntity addProductToUserCart(String email, ProductDto productDto) {
        try {
            User user = findUserByEmail(email);
            Product product = productService.findProductById(productDto.getId());

            if (!product.getAvailable()) {
                return ResponseEntity.badRequest().body("Product is no longer available");
            }

            Invoice invoice = user.getActiveInvoice();
            for (int i = 0; i < productDto.getQuantity(); i++) {
                invoice.addProduct(product);
            }

            invoiceRepository.save(invoice);
            return ResponseEntity.ok().build();
        } catch (AppException a) {
            return ResponseEntity.status(a.getCode()).body(a.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e);
        }
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



    /**
     * Retrieves the current shopping cart of a user, which includes all products and tickets in the active invoice.
     * <p>
     * This method fetches the user's active invoice (cart), removes any unavailable products from the cart, and returns the
     * current list of products and tickets in the user's cart as a {@link CartDto} object. The cart details are
     * retrieved after ensuring that all inactive products are removed.
     * </p>
     *
     * <p>
     * <strong>Warning:</strong> The handling of products in this method is currently outdated and may not be in active use.
     * This is because currently, 24/12/2024, the products are managed in the sessiondata in the frontend.
     * Products are only placed in the invoice when the transaction is being completed. The effect of which leads
     *  to the handling of products section in this method is no longer being used.
     * </p>
     *
     * @param email The email of the user whose cart is to be fetched.
     * @return {@link ResponseEntity} containing:
     * <ul>
     *     <li>{@code 200 OK} with a {@link CartDto} object containing the user's cart details if successful.</li>
     *     <li>{@code 400 Bad Request} if there is a user-related issue or an application-specific exception occurs.</li>
     *     <li>{@code 500 Internal Server Error} if an unexpected error occurs during the cart retrieval process.</li>
     * </ul>
     */
    @Override
    public ResponseEntity<?> getCart(String email) {
        try {
            User user = findUserByEmail(email);
            removeUnavailableProductsFromCart(user);  // Removes any unavailable products from the cart

            Invoice invoice = user.getActiveInvoice();  // Retrieves the user's active invoice
            invoiceRepository.save(invoice);  // Saves the updated invoice

            // Returns the user's cart details as a CartDto containing products and tickets
            return ResponseEntity.ok(new CartDto(invoice.getProducts(), invoice.getTickets()));
        } catch (AppException a) {
            return ResponseEntity.status(a.getCode()).body(a.getMessage());  // Handles app-specific exception
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e);  // Handles unexpected errors
        }
    }


    /**
     * Clears the user's cart by removing all products and tickets.
     * <p>
     * This method retrieves the user's active invoice (cart) and removes all products and tickets from it.
     * After clearing the cart, the updated invoice is saved to the database to reflect the changes.
     * </p>
     * <p>
     * <strong>Warning:</strong> The handling of products in this method is currently outdated and may not be in active use.
     * This is because currently, 24/12/2024, the products are managed in the sessiondata in the frontend.
     * Products are only placed in the invoice when the transaction is being completed. The effect of which leads
     *  to the handling of products section in this method is no longer being used.
     * </p>
     * @param email The email of the user whose cart is to be cleared.
     * @return {@code ResponseEntity} with:
     * <ul>
     *     <li>{@code 200 OK} if the cart is successfully cleared.</li>
     *     <li>{@code 400 Bad Request} if the user is not found or any application-specific error occurs.</li>
     *     <li>{@code 500 Internal Server Error} if an unexpected error occurs during the cart clearing process.</li>
     * </ul>
     */
    @Override
    @Transactional
    public ResponseEntity clearCart(String email) {
        try {
            User user = findUserByEmail(email);
            Invoice invoice = user.getActiveInvoice();
            invoice.getProducts().clear();  // Clears all products from the cart
            invoice.getTickets().clear();   // Clears all tickets from the cart
            invoiceRepository.save(invoice);  // Saves the updated invoice
            return ResponseEntity.ok().build();
        } catch (AppException a) {
            return ResponseEntity.status(a.getCode()).body(a.getMessage());  // Handles app-specific exception
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());  // Handles unexpected errors
        }
    }


    /**
     * Removes a ticket from the user's cart.
     * <p>
     * This method removes the specified ticket from the user's active invoice (cart).
     * If the ticket or user is not found, an exception will be thrown. After the removal,
     * the updated invoice is saved to reflect the changes.
     * </p>
     *
     * @param email The email of the user whose cart the ticket is being removed from.
     * @param id The ID of the ticket to be removed from the cart.
     * @return {@code ResponseEntity} with:
     * <ul>
     *     <li>{@code 200 OK} if the ticket is successfully removed.</li>
     *     <li>{@code 400 Bad Request} if the ticket or user is not found, or any application-specific error occurs.</li>
     *     <li>{@code 500 Internal Server Error} if an unexpected error occurs during the process.</li>
     * </ul>
     */
    @Override
    public ResponseEntity removeTicket(String email, Long id) {
        try {
            User user = findUserByEmail(email);
            Invoice invoice = user.getActiveInvoice();
            invoice.removeTicket(ticketService.findTicketById(id));  // Removes ticket by ID
            invoiceRepository.save(invoice);  // Saves the updated invoice
            return ResponseEntity.ok().build();
        } catch (AppException a) {
            return ResponseEntity.status(a.getCode()).body(a.getMessage());  // Handles app-specific exception
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());  // Handles unexpected errors
        }
    }


    /**
     * Removes a product from the user's cart.
     * <p>
     * This method removes the specified product from the user's active invoice (cart).
     * If the product or user is not found, an exception will be thrown.
     * After the removal, the updated invoice is saved to reflect the changes.
     * </p>
     *
     * <p>
     * <strong>Warning:</strong> This method is currently outdated and may not be in active use.
     * This is because currently, 24/12/2024, the products are managed in the sessiondata in the frontend.
     * Products are only placed in the invoice when the transaction is being completed. The effect of which leads
     *  to the method no longer being used.
     * </p>
     *
     * @param email The email of the user whose cart the product is being removed from.
     * @param id The ID of the product to be removed from the cart.
     * @return {@code ResponseEntity} with:
     * <ul>
     *     <li>{@code 200 OK} if the product is successfully removed.</li>
     *     <li>{@code 400 Bad Request} if the product or user is not found or any application-specific error occurs.</li>
     *     <li>{@code 500 Internal Server Error} if an unexpected error occurs during the process.</li>
     * </ul>
     */
    @Override
    public ResponseEntity removeProduct(String email, Long id) {
        try {
            User user = findUserByEmail(email);
            Invoice invoice = user.getActiveInvoice();
            invoice.removeProduct(productService.findProductById(id));  // Removes product by ID
            invoiceRepository.save(invoice);  // Saves the updated invoice
            return ResponseEntity.ok().build();
        } catch (AppException a) {
            return ResponseEntity.status(a.getCode()).body(a.getMessage());  // Handles app-specific exception
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());  // Handles unexpected errors
        }
    }


    /**
     * Removes all unavailable products from the user's cart and throws an exception if any inactive products are found.
     * <p>
     * This method checks the user's active invoice (cart) for any products that are marked as unavailable.
     * If such products are found, they are removed from the cart, and an exception is thrown to indicate that inactive
     * products were present in the cart. The updated cart (invoice) is saved after removing the unavailable products.
     * </p>
     *
     * <p>
     * The method first checks if there are any products in the user's active invoice that are unavailable. If such products are found,
     * they are removed from the invoice, and the IDs of the removed products are collected. The invoice is then saved with the updated list of products.
     * Finally, an {@link AppException} is thrown, indicating that inactive products were present and have been removed, with the product IDs included in the message.
     * </p>
     *
     * @param user The user whose cart is to be checked and updated.
     *             The user's active invoice is used to check for unavailable products.
     * @throws AppException If inactive products are found in the cart, an exception is thrown with a {@code FORBIDDEN} status.
     *                      The exception message will contain the IDs of the removed inactive products.
     */
    @Override
    public void removeUnavailableProductsFromCart(User user) {
        if (user.getActiveInvoice().getProducts().stream().anyMatch(product -> !product.getAvailable())) {
            Invoice invoice = user.getActiveInvoice();

            // Collect the products that are inactive
            List<Product> inactiveProducts = invoice.getProducts().stream()
                    .filter(product -> !product.getAvailable())
                    .collect(Collectors.toList());

            // Collect the IDs of inactive products
            List<Long> inactiveProductIds = inactiveProducts.stream()
                    .map(Product::getId)
                    .collect(Collectors.toList());

            // Remove inactive products from the invoice
            inactiveProducts.forEach(product -> invoice.removeProduct(product));

            // Save the updated invoice
            invoiceRepository.save(invoice);

            // Throw exception with the product IDs in the message
            throw new AppException(
                    "Had inactive products in cart with IDs: " + inactiveProductIds.toString() + ", are now removed",
                    HttpStatus.FORBIDDEN
            );
        }
    }




    /**
     * Processes the payment for the products in the user's cart.
     * <p>
     * This method processes the payment for the products and their quantities in the users active invoice (cart).
     * It first checks for the availability of the products, and if any products are unavailable,
     * they are removed from the cart. Once all the products are validated, the quantities are added
     * to the user's active invoice and the payment is completed. After processing the payment:
     * <ul>
     *     <li>The user's active invoice is updated with the paid products.</li>
     *     <li>The invoice of the User is set to paid and the a new unpaid invoice is added to the user.</li>
     * </ul>
     * If an error occurs during any part of the payment process, the transaction is not completed.
     * </p>
     *
     * @param email             the email of the user making the payment.
     * @param productPayments   a list of product payment details, each containing the product ID and the quantity to be paid for.
     * @return {@code ResponseEntity} with:
     * <ul>
     *     <li>{@code 200 OK} if the payment is successful.</li>
     *     <li>{@code 400 Bad Request} if there is an issue with the products (e.g., unavailable products or users, invalid details,...).</li>
     *     <li>{@code 500 Internal Server Error} if an unexpected error occurs during the payment process.</li>
     * </ul>
     */
    @Override
    public ResponseEntity pay(String email, List<ProductPaymentDTO> productPayments) {
        try {
            User user = findUserByEmail(email);  // Retrieves the user based on email
            removeUnavailableProductsFromCart(user);  // Removes any unavailable products from the user's cart

            // Process each product payment, adding the respective quantity to the user's invoice
            for (ProductPaymentDTO payment : productPayments) {
                Product product = productService.findProductById(payment.getProductId());  // Finds the product by ID
                if(!product.getAvailable()){
                    throw new AppException("Had inactive products in cart, are now removed", HttpStatus.FORBIDDEN);  // Throws exception
                }
                for (int i = 0; i < payment.getQuantity(); i++) {
                    user.getActiveInvoice().addProduct(product);  // Adds the product to the active invoice
                }
            }

            // Save the updated invoice and mark the user as having completed the payment
            invoiceRepository.save(user.getActiveInvoice());
            user.pay();  // Marks the payment as completed, will set the Invoice as paid an creates a new unpaid one
            userRepository.save(user);  // Saves the updated user status

            return ResponseEntity.ok().build();  // Returns a successful response
        } catch (AppException a) {
            // Handles known exceptions (e.g., product not found, etc.)
            return ResponseEntity.status(a.getCode()).body(a.getMessage());
        } catch (Exception e) {
            // Handles any unexpected errors during processing
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
