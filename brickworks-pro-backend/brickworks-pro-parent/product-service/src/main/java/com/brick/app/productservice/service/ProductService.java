package com.brick.app.productservice.service;

import com.brick.app.productservice.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    public List<Product> getAllProducts();

    public Optional<Product> getProductById(Long id);

    public Product createProduct(Product product, MultipartFile imageFile) throws IOException;

    public void deleteProduct(Long id);

    public Product updateProduct(Long id, Product product, MultipartFile imageFile) throws IOException;
}
