package com.example.backend.controller;

import com.example.backend.dto.ProductDto;
import com.example.backend.enums.Category;
import com.example.backend.model.Product;
import com.example.backend.service.impl.ProductServiceImpl;
import com.example.backend.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private UserServiceImpl userServiceImpl;

    /**
     *  creating a new product.
     * <p>
     * This route allows the admin to create a new product by providing the product details and an image file.
     * The product data is encapsulated in a {@link ProductDto} object, and the image file is expected to be a valid image.
     * </p>
     *
     * @param productDto {@link ProductDto} containing the product details.
     * @param imageFile {@link MultipartFile} representing the product's image.
     * @return {@link ResponseEntity} indicating the result of the operation.
     */
    @PostMapping(value = "/admin/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity createProduct(@RequestPart ProductDto productDto, @RequestPart MultipartFile imageFile) {
        return productService.createProduct(productDto, imageFile);
    }

    /**
     * Endpoint for updating a product's details and its associated image.
     * <p>
     * This route is for admin users to update a product by providing the product ID and the new product details.
     * The image file must be a valid image.
     * </p>
     *
     * @param id {@link Long} representing the ID of the product to update.
     * @param productDto {@link ProductDto} containing the updated product information.
     * @param imageFile {@link MultipartFile} representing the updated product image.
     * @return {@link ResponseEntity} indicating the result of the update operation.
     * @throws IOException If an error occurs while handling the image file.
     */
    @PutMapping("/admin/products/{id}/img")
    public ResponseEntity update(@PathVariable Long id, @RequestPart ProductDto productDto, @RequestPart MultipartFile imageFile) throws IOException {
        return productService.updateById(id, productDto, imageFile);
    }

    /**
     * Endpoint for updating a product's details without updating its image.
     * <p>
     * This route allows admins to modify a product's attributes (e.g., name, price, availability) without changing the image.
     * </p>
     *
     * @param id {@link Long} representing the ID of the product to update.
     * @param productDto {@link ProductDto} containing the updated product details.
     * @return {@link ResponseEntity} indicating the result of the update operation.
     */
    @PutMapping("/admin/products/{id}")
    public ResponseEntity update(@PathVariable Long id, @RequestPart ProductDto productDto) {
        return productService.updateById(id, productDto);
    }

    /**
     * Endpoint for retrieving a list of all products.
     * <p>
     * This route returns a list of all products as {@link ProductDto} objects. It is accessible by the public.
     * </p>
     *
     * @return {@link ResponseEntity} containing a {@link List<ProductDto>} if successful.
     */
    @GetMapping("/public/products")
    public ResponseEntity<?> findAll() {
        return productService.getAllProductDtos();
    }

    /**
     * Endpoint for retrieving a specific product's details by its ID, accessible to the public.
     * <p>
     * This method provides basic product information such as name, price, and description.
     * For more detailed information, the admin version of this route can be used.
     * </p>
     *
     * @param id {@link Long} representing the product ID.
     * @return {@link ResponseEntity} containing the {@link ProductDto} if successful.
     * @see #adminFindById(Long) for the admin version with more detailed information.
     */
    @GetMapping("public/products/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return productService.getProductDtoById(id);
    }

    /**
     * Endpoint for retrieving detailed product information for admins, including additional product data.
     * <p>
     * Admin users can access this endpoint to retrieve comprehensive details about a product,
     * such as internal metadata, that is not available to the general public.
     * </p>
     *
     * @param id {@link Long} representing the product ID.
     * @return {@link ResponseEntity} containing the {@link ProductDto} if successful.
     * @see #findById(Long) for the public version that provides basic product details.
     */
    @GetMapping("admin/products/{id}")
    public ResponseEntity adminFindById(@PathVariable Long id) {
        return productService.adminFindById(id);
    }

    /**
     * Endpoint for deleting a product by its ID.
     * <p>
     * This route allows the admin to delete a product from the system. The product is identified by its ID,
     * and the operation is performed without returning any content upon success.
     * </p>
     *
     * @param id {@link Long} representing the product ID to delete.
     * @return {@link ResponseEntity} indicating the result of the delete operation.
     */
    @DeleteMapping("admin/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        return productService.deleteById(id);
    }

    /**
     * Endpoint for adding a product to the authenticated user's shopping cart.
     * <p>
     * This route allows an authenticated user to add a product to their cart. The user is identified
     * via their authentication token, and the product is added based on the details provided in the {@link ProductDto}.
     * </p>
     *
     * @param productDto {@link ProductDto} containing the product details to add to the cart.
     * @return {@link ResponseEntity} indicating the result of the add operation.
     */
    @PostMapping("/secure/products")
    public ResponseEntity addProductToCart(@RequestBody ProductDto productDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get the logged-in username
        return userServiceImpl.addProductToUserCart(username, productDto);
    }

    /**
     * Endpoint for toggling the availability status of a product.
     * <p>
     * This route allows an admin to enable or disable the availability of a product based on its ID.
     * </p>
     *
     * @param id {@link Long} representing the product ID whose availability is to be toggled.
     * @return {@link ResponseEntity} indicating the result of the operation.
     */
    @PutMapping("/admin/products/{id}/availability")
    public ResponseEntity manageAvailabilityProduct(@PathVariable Long id) {
        return productService.availabilityProduct(id);
    }

    /**
     * Endpoint for retrieving a list of all available product categories.
     * <p>
     * This route returns all the categories defined in the {@link Category} enum.
     * </p>
     *
     * @return {@link List<String>} representing the available categories as strings.
     */
    @GetMapping("/public/categories")
    public List<String> getCategories() {
        return Arrays.stream(Category.values())
                .map(Enum::name)
                .toList();
    }

    /**
     * Endpoint for retrieving the image of a product by its ID.
     * <p>
     * This route allows the public to fetch the image associated with a specific product,
     * identified by the product ID.
     * </p>
     *
     * @param id {@link Long} representing the product ID.
     * @return {@link ResponseEntity} containing the image file if successful.
     */
    @GetMapping("/public/products/{id}/image")
    public ResponseEntity<?> getImageByProductId(@PathVariable long id) {
        return productService.getImg(id);
    }

}
