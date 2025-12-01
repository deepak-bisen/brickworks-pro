package com.brick.app.productservice.controller;

import com.brick.app.productservice.model.Product;
import com.brick.app.productservice.repository.ProductRepository;
import com.brick.app.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)  // Sets the HTTP status code to 201 Created on success
    public ResponseEntity<?> createProduct(@RequestPart("product") Product product,@RequestPart(value = "imageFile", required = false) MultipartFile imageFile) { // @RequestBody tells Spring to convert the incoming JSON into a Product object
        try {
            Product saveProduct = productService.createProduct(product, imageFile);
            return new ResponseEntity<>(saveProduct, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a product by its ID.
     * This is a protected endpoint and requires an authenticated JWT.
     *
     * @param id The ID of the product to delete.
     * @return A 204 No Content response on success, or 404 Not Found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            System.out.println("deletion successful");
            // Return a 204 No Content status, which is the standard for a successful DELETE.
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // If the service throws an exception (e.g., "Product not found"),
            // return a 404 Not Found.
            System.out.println("deletion not done!");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Updates an existing product by its ID.
     * This is a protected endpoint and requires an authenticated JWT.
     *
     * @param id             The ID of the product to update.
     * @param product The new product data from the request body.
     * @return A 200 OK response with the updated product, or 404 Not Found.
     */
    @PutMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestPart("product") Product product,@RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            Product updatedProduct = productService.updateProduct(id, product, imageFile);
            // Return 200 OK with the updated product
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            // If the service throws "Product not found", return 404
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

/**
 * Create a new product with an optional image file.
 * Consumes MULTIPART_FORM_DATA_VALUE.
 * The 'product' part is expected to be a JSON string which Spring converts to a Product object.
 * The 'imageFile' part is the binary file data.
 */
/**
 * @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
 * @ResponseStatus(HttpStatus.CREATED) public ResponseEntity<?> createProduct(
 * @RequestPart("product") Product product,
 * @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
 * <p>
 * try {
 * // We delegate to the service.
 * // Note: We need to update ProductService to handle the file processing logic
 * // For now, assuming ProductService has a method that handles this or we do it here.
 * <p>
 * // If your service doesn't support file processing yet, we do a quick adaptation here:
 * // (Ideally, move this logic into ProductService)
 * if (imageFile != null && !imageFile.isEmpty()) {
 * // Convert image to Base64 string
 * String base64Image = "data:" + imageFile.getContentType() + ";base64," +
 * java.util.Base64.getEncoder().encodeToString(imageFile.getBytes());
 * product.setImageUrl(base64Image);
 * }
 * <p>
 * Product savedProduct = productService.createProduct(product);
 * return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
 * <p>
 * } catch (Exception e) {
 * return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
 * }
 * }
 */