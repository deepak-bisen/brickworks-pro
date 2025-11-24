package com.brick.app.productservice.service;

import com.brick.app.productservice.dto.ProductDTO;
import com.brick.app.productservice.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    public List<Product> getAllProducts();

    public Optional<Product> getProductById(Long id);

    public Product createProduct(Product product);

    public void deleteProduct(Long id);

    public Product updateProduct(Long id, Product productDetails);
}
