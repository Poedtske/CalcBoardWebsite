package com.example.backend.service.impl;

import com.example.backend.dto.ProductDto;
import com.example.backend.exceptions.AppException;
import com.example.backend.model.Product;
import com.example.backend.repository.InvoiceRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    //dir for product images
    private static String IMG_DIR=System.getProperty("user.dir") + "/backend/src/main/resources/images/products";
    //path for product images
    private static Path IMGS_PATH=Paths.get(IMG_DIR);

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;

    /**
     * saves a product to the DB
     * @return #{@code List<Product>}
     */
    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    /**
     * Updates an existing product by its ID, including the image.
     * <p>
     * This method updates the specified product's attributes such as name, price, availability, category,
     * and replaces its associated image with a new one.
     * It ensures that the product cannot be updated if it has invoices that are:
     * <ul>
     *     <li>Paid but not closed.</li>
     * </ul>
     * If such invoices exist, the update operation is denied.
     * It also deletes the old image from storage and saves the new image with a filename based on the product name and ID.
     * </p>
     *
     * @param id          the ID of the product to be updated.
     * @param productDto  the data transfer object containing updated product details.
     * @param imageFile   the new image file to replace the existing image.
     * @return {@code ResponseEntity} with:
     * <ul>
     *     <li>{@code 200 OK} if the update is successful.</li>
     *     <li>{@code 400 Bad Request} if the product is invalid or an application-specific exception occurs.</li>
     *     <li>{@code 404 Not Found} if the existing image file does not exist.</li>
     *     <li>{@code 500 Internal Server Error} if an unexpected error occurs or if there are unclosed paid invoices.</li>
     * </ul>
     *
     * @throws AppException if the product is invalid or cannot be found.
     * @throws IOException if there are issues with file operations during image update.
     * @throws Exception for any unexpected errors during processing.
     */
    @Override
    public ResponseEntity updateById(Long id, ProductDto productDto, MultipartFile imageFile) {
        try {
            // Find the product by ID
            Product managedProduct = findProductById(id);

            // Check for unclosed paid invoices
            boolean hasUnclosedPaidInvoice = managedProduct.getInvoices().stream()
                    .anyMatch(invoice -> (invoice.getPaid() == true && invoice.getClosed() == false));

            if (hasUnclosedPaidInvoice) {
                // Return error if unclosed paid invoices exist
                return ResponseEntity.internalServerError().body(
                        "Cannot be updated, there are invoices that have been paid and are still open"
                );
            }

            // Update product details
            managedProduct.setName(productDto.getName());
            managedProduct.setPrice(productDto.getPrice());
            managedProduct.setAvailable(productDto.getAvailable());
            managedProduct.setCategory(productDto.getCategory());

            // Validate the existence of the image directory
            if (!Files.exists(IMGS_PATH)) {
                return ResponseEntity.internalServerError().body("Image directory not found.");
            }

            // Delete the current image
            Path filePath = IMGS_PATH.resolve(managedProduct.getImg());
            if (Files.exists(filePath)) {
                Files.deleteIfExists(filePath); // Delete old image
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Current image not found.");
            }

            // Save the new image
            // Generate a new filename and construct the file path
            String newFilename = managedProduct.getName() + "_" + managedProduct.getId() + "." +
                    StringUtils.getFilenameExtension(imageFile.getOriginalFilename());
            Path newFilePath = IMGS_PATH.resolve(newFilename);

            // Write the new image file
            Files.write(newFilePath, imageFile.getBytes());

            // Update the product's image path
            managedProduct.setImg(newFilename);

            // Save updated product
            this.save(managedProduct);

            // Return success response
            return ResponseEntity.ok().build();

        } catch (AppException a) {
            // Return 400 error for application-specific exceptions
            return ResponseEntity.status(a.getCode()).body(a.getMessage());
        } catch (IOException e) {
            // Handle file-related exceptions
            return ResponseEntity.internalServerError().body("File processing error: " + e.getMessage());
        } catch (Exception e) {
            // Return 500 error for unexpected exceptions
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Updates an existing product by its ID, excluding the image field.
     * <p>
     * This method updates the specified product's attributes such as name, price, availability, and category.
     * However, it does not update the product's associated image.
     * It also ensures that the product cannot be updated if it has invoices that are:
     * <ul>
     *     <li>Paid but not closed.</li>
     * </ul>
     * If such invoices exist, the update operation is denied.
     * </p>
     *
     * @param id          the ID of the product to be updated.
     * @param productDto  the data transfer object containing updated product details.
     * @return {@code ResponseEntity} with:
     * <ul>
     *     <li>{@code 200 OK} if the update is successful.</li>
     *     <li>{@code 400 Bad Request} if the product is invalid or an application-specific exception occurs.</li>
     *     <li>{@code 500 Internal Server Error} if an unexpected error occurs or if there are unclosed paid invoices.</li>
     * </ul>
     */
    @Override
    public ResponseEntity updateById(Long id, ProductDto productDto) {
        try {
            // Find the product by ID
            Product managedProduct = findProductById(id);

            // Check for unclosed paid invoices
            boolean hasUnclosedPaidInvoice = managedProduct.getInvoices().stream()
                    .anyMatch(invoice -> (invoice.getPaid() == true && invoice.getClosed() == false));

            if (hasUnclosedPaidInvoice) {
                // Return error if unclosed paid invoices exist
                return ResponseEntity.internalServerError().body(
                        "Cannot be updated, there are invoices that have been paid and are still open"
                );
            }

            // Update product details
            managedProduct.setName(productDto.getName());
            managedProduct.setPrice(productDto.getPrice());
            managedProduct.setAvailable(productDto.getAvailable());
            managedProduct.setCategory(productDto.getCategory());

            // Save updated product
            this.save(managedProduct);

            // Return success response
            return ResponseEntity.ok().build();

        } catch (AppException a) {
            // Return 400 error for application-specific exceptions
            return ResponseEntity.status(a.getCode()).body(a.getMessage());
        } catch (Exception e) {
            // Return 500 error for unexpected exceptions
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Retrieves a list of all available products. Is intended for only internal use
     * <p>
     * This method retrieves all products.
     * </p>
     *
     * @return #{@code List<Product>}
     */
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /**
     * Retrieves a list of all available products as Data Transfer Objects (DTOs).
     * <p>
     * This method fetches all products, converts them into {@code ProductDto} objects,
     * and returns them as a list. It includes error handling for unexpected failures.
     * </p>
     *
     * @return {@code ResponseEntity<List<ProductDto>>} containing:
     * <ul>
     *     <li>{@code 200 OK} with the list of available products.</li>
     *     <li>{@code 500 Internal Server Error} for unexpected errors.</li>
     * </ul>
     */
    public ResponseEntity<?> getAllProductDtos() {
        try {
            List<ProductDto> productDtos = findAll()
                    .stream()
                    .map(product -> new ProductDto(
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            product.getImg(),
                            product.getCategory(),
                            product.getAvailable(),
                            product.getInvoices()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(productDtos); // Return the list of DTOs
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Finds a product by its ID and checks availability status. Is meant to be used by the controller.
     * <p>
     * This method retrieves a product entity based on the provided ID. If the product
     * exists and is available, it returns the product. If the product exists but is
     * unavailable, it returns {@code null}. If no product is found, it throws an exception.
     * </p>
     *
     * @param id the ID of the product to find.
     * @return the product if found and available; {@code null} if found but unavailable.
     * @throws AppException if no product is found with the specified ID.
     */
    @Override
    public Product findById(Long id) {
        Product p= findProductById(id);
        if(p.getAvailable()){
            return p;
        }else{
            return null;
        }
    }

    /**
     * Finds a product by its ID and checks availability status.
     * <p>
     * This method retrieves a product entity based on the provided ID. If the product
     * exists and is available, it returns the product. If the product exists but is
     * unavailable, it returns {@code null}. If no product is found, it throws an exception.
     * </p>
     *
     * @param id the ID of the product to find.
     * @throws AppException if no product is found with the specified ID.
     */
    public Product findProductById(Long id) {
        return productRepository.findById(id).orElseThrow(()-> new AppException("Product not found",HttpStatus.NOT_FOUND));
    }

    /**
     * Retrieves the details of a product as a Data Transfer Object (DTO) by its ID.
     * <p>
     * This method fetches a product using its ID, converts it into a {@code ProductDto}, and returns it.
     * It provides error handling for cases where the product cannot be found or other exceptions occur.
     * </p>
     *
     * @param id the ID of the product to be retrieved.
     * @return {@code ResponseEntity<ProductDto>} containing:
     * <ul>
     *     <li>{@code 200 OK} with the product details if successful.</li>
     *     <li>{@code 404 Not Found} if the product is unavailable (commented-out check).</li>
     *     <li>{@code 500 Internal Server Error} for unexpected errors or exceptions.</li>
     * </ul>
     */
    @Override
    public ResponseEntity<?> getProductDtoById(Long id) {
        try {
            // Retrieve the product by ID
            Product p = findProductById(id);

            // Optionally, check availability if required (currently commented out)
        /*
        if (p.getAvailable() == false) {
            throw new AppException("Product not found", HttpStatus.NOT_FOUND);
        }
        */

            // Map product entity to ProductDto
            ProductDto productDto = new ProductDto(
                    p.getId(),
                    p.getName(),
                    p.getPrice(),
                    p.getImg(),
                    p.getCategory(),
                    p.getAvailable(),
                    p.getInvoices()
            );

            // Return the DTO in the response
            return ResponseEntity.ok(productDto);

        } catch (AppException a) {
            // Handle application-specific exceptions
            return ResponseEntity.status(a.getCode()).body(a.getMessage());
        } catch (Exception e) {
            // Catch-all for unexpected errors
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Deletes a product by its ID, including associated invoices and the product's image file.
     * <p>
     * This method performs the following steps:
     * <ol>
     *     <li>Validates whether the product has invoices that are paid but still open. If such invoices exist, deletion is prevented.
     *     and returns a code 500 status.</li>
     *     <li>Processes each invoice associated with the product:
     *         <ul>
     *             <li>Unpaid invoices have the product removed from them.</li>
     *             <li>Paid and closed invoices are deleted.</li>
     *         </ul>
     *     </li>
     *     <li>Deletes the product's image file from the file system.</li>
     *     <li>Deletes the product itself from the database.</li>
     * </ol>
     * </p>
     *
     * @param id the ID of the product to be deleted.
     * @return {@code ResponseEntity<Void>} containing:
     * <ul>
     *     <li>{@code 200 OK} if the product was successfully deleted.</li>
     *     <li>{@code 400 Bad Request} if the product cannot be found.</li>
     *     <li>{@code 404 Not Found} if the product image file does not exist.</li>
     *     <li>{@code 500 Internal Server Error} if there are unclosed paid invoices or unexpected errors occur.</li>
     * </ul>
     */
    @Override
    public ResponseEntity deleteById(Long id) {
        try {
            // Retrieve the product by ID
            Product p = findProductById(id);

            // Check for unclosed, paid invoices
            boolean hasUnclosedPaidInvoice = p.getInvoices().stream()
                    .anyMatch(invoice -> (invoice.getPaid() == true && invoice.getClosed() == false));

            if (hasUnclosedPaidInvoice) {
                // Prevent deletion if there are paid but unclosed invoices
                return ResponseEntity.internalServerError().body("Cannot be deleted, there are invoices that have been paid and are still open");
            }

            // Process each invoice associated with the product
            p.getInvoices().forEach(invoice -> {
                if (invoice.getPaid() == false) { // Unpaid invoices
                    invoice.removeProduct(p); // Remove the product from the invoice
                    invoiceRepository.save(invoice);
                } else if (invoice.getPaid() == true && invoice.getClosed() == true) { // Paid and closed invoices
                    invoiceRepository.delete(invoice); // Delete the invoice
                }
            });

            // Save the updated product (with invoices modified)
            productRepository.save(p);

            // Validate the existence of the image directory
            if (!Files.exists(IMGS_PATH)) {
                return ResponseEntity.internalServerError().body(null);
            }

            // Check if the product's image file exists
            Path filePath = IMGS_PATH.resolve(p.getImg());
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Delete the product and the associated image file
            productRepository.deleteById(id);
            Files.deleteIfExists(filePath);

            // Return success response
            return ResponseEntity.ok().build();

        } catch (AppException a) {
            // Handle application-specific exceptions (e.g., product not found)
            return ResponseEntity.status(a.getCode()).body(a.getMessage());
        } catch (IOException io) {
            // Handle file-related exceptions
            return ResponseEntity.internalServerError().body("File error: " + io.getMessage());
        } catch (Exception e) {
            // Catch-all for unexpected errors
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Creates a new product and associates it with an image file.
     * <p>
     * This method saves a new product to the database and stores the associated image in the file system.
     * The image file is validated for correct format (JPG, JPEG, PNG, or GIF), and the product is updated with
     * the image path once the image is saved successfully.
     * </p>
     *
     * @param productDto the DTO containing the product details including name, price, availability, quantity, and category.
     * @param imageFile the image file to be uploaded and associated with the product.
     * @return {@code ResponseEntity<Product>} containing:
     * <ul>
     *     <li>{@code 201 Created} with the created product if successful.</li>
     *     <li>{@code 400 Bad Request} if the image file format is invalid or product data is incorrect.</li>
     *     <li>{@code 500 Internal Server Error} for any unexpected issues such as file handling or database errors.</li>
     * </ul>
     */
    @Override
    public ResponseEntity createProduct(ProductDto productDto, MultipartFile imageFile) {
        try {
            // Ensure the image directory exists
            if (!Files.exists(IMGS_PATH)) {
                Files.createDirectories(IMGS_PATH);
            }

            // Validate the image file (check content type)
            String originalFilename = imageFile.getOriginalFilename();
            if (originalFilename == null || !originalFilename.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|avif)")) {
                return ResponseEntity.badRequest().body("Invalid image format. Only JPG, JPEG, PNG, GIF and AVIF are allowed.");
            }

            // Save product initially without the image path to generate the ID
            Product product = new Product(
                    productDto.getName(),
                    productDto.getPrice(),
                    "", // Temporarily empty image path
                    productDto.getAvailable(),
                    productDto.getQuantity(),
                    productDto.getCategory()
            );
            product = productRepository.save(product); // Save product to get ID

            // Generate the new filename and construct the file path
            String newFilename = product.getName() + "_" + product.getId() + "." +
                    StringUtils.getFilenameExtension(originalFilename);
            Path newFilePath = IMGS_PATH.resolve(newFilename);

            // Write the file with the new name
            Files.write(newFilePath, imageFile.getBytes());

            // Update the product with the relative path to the image
            product.setImg(newFilename);
            productRepository.save(product); // Save updated product

            return new ResponseEntity<>(product, HttpStatus.CREATED);

        } catch (AppException a) {
            // Return 400 with the exception message if the product isn't valid
            return ResponseEntity.status(a.getCode()).body(a.getMessage());
        } catch (IOException io) {
            // Return 500 for file system errors
            return ResponseEntity.internalServerError().body("File error: " + io.getMessage());
        } catch (Exception e) {
            // Return 500 for any other errors
            return ResponseEntity.internalServerError().body("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Retrieves an image file associated with a product by its ID.
     * <p>
     * This method fetches the image file for a given product and serves it as a downloadable resource.
     * It handles file existence.
     * </p>
     *
     * @param id the ID of the product whose image is to be retrieved.
     * @return {@code ResponseEntity<Resource>} containing:
     * <ul>
     *     <li>{@code 200 OK} with the image resource if successful.</li>
     *     <li>{@code 400 Bad Request} if the product is not found.</li>
     *     <li>{@code 404 Not Found} if the image file does not exist.</li>
     *     <li>{@code 500 Internal Server Error} for unexpected issues.</li>
     * </ul>
     */
    @Override
    public ResponseEntity getImg(Long id) {
        try {
            // Retrieve the product by ID
            Product p = findProductById(id);

            // Validate the image name
            if (p.getImg() == null || p.getImg().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }

            // Resolve and check the file path
            Path filePath = IMGS_PATH.resolve(p.getImg());
            if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }

            // Load the file as a resource
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }

            // Determine content type or fallback to octet-stream
            String contentType = Files.probeContentType(filePath);
            if (StringUtils.isEmpty(contentType)) {
                contentType = "application/octet-stream";
            }

            // Return the image resource with correct content type
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (AppException a) {
            // Handle application-specific exceptions (e.g., product not found)
            return ResponseEntity.status(a.getCode()).body(a.getMessage());
        } catch (MalformedURLException e) {
            // Handle URL formatting errors
            return ResponseEntity.internalServerError().body(e.getMessage());
        } catch (IOException e) {
            // Handle IO-related exceptions
            return ResponseEntity.internalServerError().body(e.getMessage());
        } catch (Exception e) {
            // Catch-all for unexpected exceptions
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Retrieves product details by ID for the admin, it contains more data.
     * <p>
     * This method fetches a product by its ID, converts it into a {@link ProductDto},
     * and returns the DTO in the response body. It handles errors gracefully by
     * returning appropriate HTTP status codes based on the type of failure.
     * </p>
     *
     * @param id the ID of the product to retrieve.
     * @return {@code ResponseEntity} containing the following:
     * <ul>
     *     <li>{@code 200 OK} with the {@link ProductDto} if the product is found.</li>
     *     <li>{@code 400 Bad Request} with the exception's message if the product is not found.</li>
     *     <li>{@code 500 Internal Server Error} with the exception's message for unexpected errors.</li>
     * </ul>
     */
    @Override
    public ResponseEntity adminFindById(Long id) {
        try {
            // Retrieve the product by ID
            Product p = findProductById(id);

            // Map product entity to DTO
            ProductDto productDto = new ProductDto(
                    p.getId(),
                    p.getName(),
                    p.getPrice(),
                    p.getImg(),
                    p.getCategory(),
                    p.getAvailable(),
                    p.getInvoices()
            );

            // Return 200 OK with the product DTO
            return ResponseEntity.ok(productDto);
        } catch (AppException a) {
            // Return 400 Bad Request if product is not found
            return ResponseEntity.status(a.getCode()).body(a.getMessage());
        } catch (Exception e) {
            // Return 500 Internal Server Error for unexpected issues
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Toggles the availability status of a product based on its ID.
     * <p>
     * This method retrieves a product by its ID and toggles its availability field.
     * If the product is currently unavailable ({@code false}), it is set to available
     * ({@code true}), and vice-versa. The updated product is saved to the database.
     * </p>
     * <p>
     * If the product is not found, it returns a {@code 400 Bad Request} response
     * containing the error message. For unexpected errors, it returns a
     * {@code 500 Internal Server Error} with the exception message in the response body.
     * </p>
     *
     * @param id the ID of the product whose availability is to be toggled.
     * @return {@code ResponseEntity} indicating the result:
     * <ul>
     *     <li>{@code 200 OK} if the update is successful.</li>
     *     <li>{@code 400 Bad Request} if the product is not found.</li>
     *     <li>{@code 500 Internal Server Error} for unexpected errors.</li>
     * </ul>
     */
    @Override
    public ResponseEntity availabilityProduct(Long id){
        try{
            //get the id, may throw Appexception when it's not found
            Product p= findProductById(id);

            //toggle the available field in product to false if it's true
            //and to true if its false
            if(p.getAvailable()==false){
                p.setAvailable(true);
            }else{
                p.setAvailable(false);
            }

            //save changes to the DB
            productRepository.save(p);

            //returns 200 if the toggle is successful
            return ResponseEntity.ok().build();
        }catch (AppException a){
            //returns 400 with the exceptions message in the body if the product isn't found
            return ResponseEntity.status(a.getCode()).body(a.getMessage());
        }
        catch (Exception e){
            //returns 500 with the exceptions message in the body if anything unexpected goes wrong
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
