package com.example.product_management.service;

import com.example.product_management.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    List<Product> getAllProducts();
    
    Optional<Product> getProductById(Long id);
    
    Product saveProduct(Product product);
    
    void deleteProduct(Long id);
    
    List<Product> searchProducts(String keyword);


    // Task 5 - Advanced Search
    // Task 5.1 - Multi-Criteria Search
    List<Product> advancedSearch(String name,
                             String category,
                             BigDecimal minPrice,
                             BigDecimal maxPrice);


    boolean isProductCodeDuplicate(Product product);

   
    List<Product> getProductsByCategory(String category);

    // Task 5.2 - Retrieve All Unique Categories
    List<String> getAllCategories();

    //Task 5.3 - Paginated Search
    Page<Product> searchProducts(String keyword, Pageable pageable);


}
