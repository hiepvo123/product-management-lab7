package com.example.product_management.service;

import com.example.product_management.entity.Product;
import com.example.product_management.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    @Override
    public Product saveProduct(Product product) {
        // Validation logic can go here
        return productRepository.save(product);
    }
    
    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContaining(keyword);
    }
    
    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    // Task 5.1 - Multi-Criteria Search
    @Override
    public List<Product> advancedSearch(String name,
                                    String category,
                                    BigDecimal minPrice,
                                    BigDecimal maxPrice) {
    // Có thể trim string để tránh khoảng trắng
    if (name != null && name.isBlank()) {
        name = null;
    }
    if (category != null && category.isBlank()) {
        category = null;
    }
    return productRepository.searchProducts(name, category, minPrice, maxPrice);
    }

    // Task 5.2 - Retrieve All Unique Categories
    @Override
    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }

    // Task 5.3 - Paginated Search
    @Override
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContaining(keyword, pageable);
    }

    @Override
    public boolean isProductCodeDuplicate(Product product) {
        String productCode = product.getProductCode();

        if (product.getId() == null) {
            return productRepository.existsByProductCode(productCode);
        }

        return productRepository.existsByProductCodeAndIdNot(productCode, product.getId());
    }

}
