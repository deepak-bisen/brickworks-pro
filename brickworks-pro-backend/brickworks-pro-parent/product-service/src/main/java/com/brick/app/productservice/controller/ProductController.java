package com.brick.app.productservice.controller;

import com.brick.app.productservice.model.Product;
import com.brick.app.productservice.repository.ProductRepository;
import com.brick.app.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id){
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)  // Sets the HTTP status code to 201 Created on success
    public Product createProduct(@RequestBody Product product){ // @RequestBody tells Spring to convert the incoming JSON into a Product object
        return productRepository.save(product);
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
     * @param id The ID of the product to update.
     * @param productDetails The new product data from the request body.
     * @return A 200 OK response with the updated product, or 404 Not Found.
     */
    @PutMapping("{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDetails);
            // Return 200 OK with the updated product
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            // If the service throws "Product not found", return 404
            return ResponseEntity.notFound().build();
        }
    }
}