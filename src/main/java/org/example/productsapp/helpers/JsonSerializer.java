package org.example.productsapp.helpers;

import org.example.productsapp.models.Product;

import java.util.List;

public class JsonSerializer {
    public static String serializeProduct(Product product) {
        return "{ \"id\" : " + product.getId() +
                ", \"name\" : \"" + product.getName() +
                "\", \"price\" : \"" + product.getPrice() + "\"}";
    }
    public static String serializeProducts(List<Product> products) {
        String json = "[";
        for (Product product : products) {
            json += serializeProduct(product);
            json += ",";
        }
        json = json.substring(0, json.length() - 1) + "]";
        return json;
    }
}
