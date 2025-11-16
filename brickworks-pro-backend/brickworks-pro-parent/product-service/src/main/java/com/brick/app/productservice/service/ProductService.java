package com.brick.app.productservice.service;

import com.brick.app.productservice.model.Product;

public interface ProductService {
    public void deleteProduct(Long id);
    public Product updateProduct(Long id, Product productDetails);
}
