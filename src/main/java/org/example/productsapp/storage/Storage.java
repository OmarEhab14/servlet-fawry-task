package org.example.productsapp.storage;

import org.example.productsapp.models.Product;

import java.util.List;

public interface Storage {
    Product getProduct(Integer productId);
    List<Product> getProducts();
    void addProduct(Product product);
    void updateProduct(Product product);
    void removeProduct(Integer productId);
}
