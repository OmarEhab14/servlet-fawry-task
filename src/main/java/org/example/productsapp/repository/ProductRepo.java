package org.example.productsapp.repository;

import org.example.productsapp.models.Product;

import java.util.List;

public interface ProductRepo {
    Product findById(Integer id);
    List<Product> findAll();
    void addProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(Integer id);
}
