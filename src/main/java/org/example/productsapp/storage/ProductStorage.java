package org.example.productsapp.storage;

import org.example.productsapp.models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductStorage implements Storage {
    private static Map<Integer, Product> products;
    private Integer productId;

    public ProductStorage() {
        products = new HashMap<>();
        productId = 1;
    }

    @Override
    public Product getProduct(Integer productId) {
        return products.get(productId);
    }

    @Override
    public List<Product> getProducts() {
        return new ArrayList<>(products.values());
    }

    @Override
    public void addProduct(Product product) {
        products.put(++productId, product);
    }

    @Override
    public void updateProduct(Product product) {
        products.put(product.getId(), product);
    }

    @Override
    public void removeProduct(Integer productId) {
        products.remove(productId);
    }
}
