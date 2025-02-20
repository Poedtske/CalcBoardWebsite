package com.example.backend.service;

import com.example.backend.dto.ProductDto;
import com.example.backend.exceptions.AppException;
import com.example.backend.model.Product;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing products in the system.
 * <p>
 * This interface defines methods for creating, updating, retrieving,
 * and deleting products, as well as handling product images and availability status.
 * </p>
 */
public interface ProductService {

    /**
     * Saves a given product entity to the database.
     *
     * @param product the product entity to save.
     * @return the saved {@link Product} entity.
     */
    Product save(Product product);

    /**
     * Updates an existing product by its ID, including an image update.
     *
     * @param id          the ID of the product to update.
     * @param productDto  the updated product details.
     * @param imageFile   the new image file for the product.
     * @return {@code ResponseEntity}
     */
    ResponseEntity updateById(Long id, ProductDto productDto, MultipartFile imageFile);

    /**
     * Updates an existing product by its ID without modifying the image.
     *
     * @param id         the ID of the product to update.
     * @param productDto the updated product details.
     * @return {@code ResponseEntity}
     */
    ResponseEntity updateById(Long id, ProductDto productDto);

    /**
     * Retrieves all products available in the database.
     *
     * @return a list of {@link Product} entities.
     */
    List<Product> findAll();

    /**
     * Retrieves all products as DTOs.
     *
     * @return {@code ResponseEntity} containing a list of {@link ProductDto}.
     */
    ResponseEntity<?> getAllProductDtos();

    /**
     * Finds a product by its ID.
     *
     * @param id the ID of the product to find.
     * @return the {@link Product} entity.
     */
    Product findById(Long id);

    /**
     * Finds a product by its ID and throws an exception if not found.
     *
     * @param id the ID of the product to find.
     * @return the {@link Product} entity.
     * @throws AppException if the product is not found.
     */
    Product findProductById(Long id);

    /**
     * Retrieves a product as a DTO by its ID.
     *
     * @param id the ID of the product to retrieve.
     * @return {@code ResponseEntity}
     */
    ResponseEntity getProductDtoById(Long id);

    /**
     * Deletes a product by its ID, including its associated image and invoices.
     *
     * @param id the ID of the product to delete.
     * @return {@code ResponseEntity}
     */
    ResponseEntity<Resource> deleteById(Long id);

    /**
     * Creates a new product and saves the associated image.
     *
     * @param productDto the product details to create.
     * @param imageFile  the image file to associate with the product.
     * @return {@code ResponseEntity}
     */
    ResponseEntity createProduct(ProductDto productDto, MultipartFile imageFile);

    /**
     * Retrieves the image associated with a product by its ID.
     *
     * @param id the ID of the product whose image is to be retrieved.
     * @return {@code ResponseEntity<Resource>} containing
     */
    ResponseEntity getImg(Long id);

    /**
     * Admin-only method to find a product by ID with special access rules.
     *
     * @param id the ID of the product to find.
     * @return {@code ResponseEntity} containing the product details or error status.
     */
    ResponseEntity adminFindById(Long id);

    /**
     * Toggles the availability status of a product.
     *
     * @param id the ID of the product whose availability is being updated.
     * @return {@code ResponseEntity}
     */
    ResponseEntity availabilityProduct(Long id);
}

