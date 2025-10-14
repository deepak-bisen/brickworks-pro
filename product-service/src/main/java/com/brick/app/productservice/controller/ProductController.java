package com.brick.app.productservice.controller;

import com.brick.app.productservice.model.Product;
import com.brick.app.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<Product> getAllProducts(){
    return productRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)  // Sets the HTTP status code to 201 Created on success
    public Product createProduct(@RequestBody Product product){ // @RequestBody tells Spring to convert the incoming JSON into a Product object
        return productRepository.save(product);
    }
}
