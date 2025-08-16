package org.example.productsapp.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.example.productsapp.repository.ProductRepo;
import org.example.productsapp.repositoryimpl.InMemoryProductRepoImpl;

@WebListener
public class AppStartup implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ProductRepo repo = new InMemoryProductRepoImpl();
        sce.getServletContext().setAttribute("productRepo", repo);
    }
}
