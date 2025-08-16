package org.example.productsapp.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.productsapp.helpers.JsonSerializer;
import org.example.productsapp.models.ErrorResponse;
import org.example.productsapp.models.Product;
import org.example.productsapp.repository.ProductRepo;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

@WebServlet("/products/*")
public class ProductsServlet extends HttpServlet {
    private ProductRepo productRepo;
    private final Gson gson = new Gson();

    @Override
    public void init() {
        ServletContext ctx = getServletContext();
        this.productRepo = (ProductRepo) ctx.getAttribute("productRepo");
//        productRepo.addProduct(new Product("Adidas T-Shirt", 220.0));
//        productRepo.addProduct(new Product("Burrito", 120.0));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            int id =  Integer.parseInt(pathInfo.substring(1));
            Product product = productRepo.findById(id);
            if (product != null) {
                // I manually serialized the product here, but I found out that there is a dependency called gson that can automatically serialize products for me and I used it later
                resp.getWriter().println(JsonSerializer.serializeProduct(product));
            } else {
                sendError(resp, 404, "Product not found");
            }
            return;
        }
        resp.getWriter().println(JsonSerializer.serializeProducts(productRepo.findAll()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String body = req.getReader().lines()
                .reduce("", (accumulator, actual) -> accumulator + actual);

        resp.setContentType("application/json");

        try {
            Product product = tryParseSingle(body);
            if (product != null) {
                validateProduct(product);
                productRepo.addProduct(product);
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().println(gson.toJson(product));
                return;
            }

            List<Product> products = tryParseList(body);
            if (products == null || products.isEmpty()) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
                return;
            }
            for (Product p : products) {
                validateProduct(p);
                productRepo.addProduct(p);
            }
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().println(gson.toJson(products));
        } catch (IllegalArgumentException e) {
            sendError(resp,  HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            int id =  Integer.parseInt(pathInfo.substring(1));
            Product product = productRepo.findById(id);
            if (product != null) {
                String body = req.getReader().lines()
                        .reduce("", (accumulator, actual) -> accumulator + actual);
                Product p = tryParseSingle(body);
                if (p != null) {
                    validateProduct(p);
                    p.setId(product.getId());
                    productRepo.updateProduct(p);
                    resp.getWriter().println(gson.toJson(p));
                } else {
                    sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
                }
            } else {
                sendError(resp, 404, "Product not found");
            }
        } else {
          sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Product id must be specified in the request");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            int id =  Integer.parseInt(pathInfo.substring(1));
            Product product = productRepo.findById(id);
            if (product != null) {
                productRepo.deleteProduct(id);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Product not found");
            }
        } else {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Product id must be specified in the request");
        }
    }

    // Just helper methods XD
    private Product tryParseSingle(String body) {
        try {
            return gson.fromJson(body, Product.class);
        } catch (JsonSyntaxException ex) {
            return null;
        }
    }

    private List<Product> tryParseList(String body) {
        try {
            Type listType = new TypeToken<List<Product>>(){}.getType();
            return gson.fromJson(body, listType);
        } catch (JsonSyntaxException ex) {
            return null;
        }
    }

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (product.getPrice() == null) {
            throw new IllegalArgumentException("Product price is required");
        }
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("Product price is negative");
        }
    }
    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.getWriter().println(gson.toJson(new ErrorResponse(message, statusCode)));
    }
}
