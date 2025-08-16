package org.example.productsapp.repositoryimpl;

import org.example.productsapp.models.Product;
import org.example.productsapp.repository.ProductRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryProductRepoImpl implements ProductRepo {
    private final Map<Integer, Product> products;
    private Integer productId;

    public InMemoryProductRepoImpl() {
        products = new HashMap<>();
        productId = 0;
    }

    @Override
    public Product findById(Integer id) {
        return products.get(id);
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public void addProduct(Product product) {
        product.setId(++productId);
        products.put(productId, product);
    }

    @Override
    public void updateProduct(Product product) {
        products.put(product.getId(), product);
    }

    @Override
    public void deleteProduct(Integer id) {
        products.remove(productId);
    }
}
