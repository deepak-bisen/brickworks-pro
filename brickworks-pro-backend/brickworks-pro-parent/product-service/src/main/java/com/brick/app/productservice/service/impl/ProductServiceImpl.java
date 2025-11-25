package com.brick.app.productservice.service.impl;

import com.brick.app.productservice.model.Product;
import com.brick.app.productservice.repository.ProductRepository;
import com.brick.app.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Deletes a product by its ID.
     * @param id The ID of the product to delete.
     * @throws RuntimeException if the product is not found.
     */
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            // Or you could throw a custom ResourceNotFoundException
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    /**
     * Updates an existing product.
     * @param id The ID of the product to update.
     * @param productDetails The new details for the product.
     * @return The updated product.
     * @throws RuntimeException if the product is not found.
     */
    public Product updateProduct(Long id, Product productDetails) {
        // Find the existing product
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Update the fields
        existingProduct.setName(productDetails.getName());
        existingProduct.setColor(productDetails.getColor());
        existingProduct.setBrickType(productDetails.getBrickType());
        existingProduct.setUnitPrice(productDetails.getUnitPrice());
        existingProduct.setStockQuantity(productDetails.getStockQuantity());

        // Also update the imageUrl if it's provided in the request
        existingProduct.setImageUrl(productDetails.getImageUrl());

        // Save the updated product back to the database
        return productRepository.save(existingProduct);
    }
}