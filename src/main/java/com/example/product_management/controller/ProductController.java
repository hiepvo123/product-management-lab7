package com.example.product_management.controller;

import com.example.product_management.entity.Product;
import com.example.product_management.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;


import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    
    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    // Task5.2 -- List all products
    @GetMapping
public String listProducts(
        @RequestParam(required = false) String category,
        Model model) {

    // Nếu user chọn category → lọc theo category
    List<Product> products;
    if (category != null && !category.isEmpty()) {
        products = productService.getProductsByCategory(category);
    } else {
        products = productService.getAllProducts();
    }

    // Lấy list category
    List<String> categories = productService.getAllCategories();

    model.addAttribute("products", products);
    model.addAttribute("categories", categories);
    model.addAttribute("selectedCategory", category); // để giữ state selected

    return "product-list";
}


    // ==== Task 5.1 - Multi-Criteria Search ====
    @GetMapping("/advanced-search")
public String advancedSearch(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        Model model) {

    // Nếu tất cả đều rỗng thì trả về list tất cả
    if ((name == null || name.isBlank()) &&
        (category == null || category.isBlank()) &&
        minPrice == null &&
        maxPrice == null) {

        model.addAttribute("products", productService.getAllProducts());
    } else {
        model.addAttribute("products",
                productService.advancedSearch(name, category, minPrice, maxPrice));
    }

    // Đưa lại giá trị user đã nhập để giữ trên form
    model.addAttribute("name", name);
    model.addAttribute("category", category);
    model.addAttribute("minPrice", minPrice);
    model.addAttribute("maxPrice", maxPrice);

    return "product-list"; // dùng lại cùng template list
}

// ==== Task 5.3 - Paginated Search ====
@GetMapping("/search")
public String searchProducts(
        @RequestParam("keyword") String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        Model model) {

    Pageable pageable = PageRequest.of(page, size);
    Page<Product> productPage = productService.searchProducts(keyword, pageable);

    model.addAttribute("products", productPage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", productPage.getTotalPages());
    model.addAttribute("keyword", keyword);

    return "product-list";
}


    
    // Show form for new product
    @GetMapping("/new")
    public String showNewForm(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        return "product-form";
    }
    
    // Show form for editing product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return productService.getProductById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    return "product-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Product not found");
                    return "redirect:/products";
                });
    }
    
    // Save product (create or update)
    @PostMapping("/save")
    public String saveProduct(
        @Valid @ModelAttribute("product") Product product,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
        return "product-form";   // Quay lại form nếu lỗi
    }

    try {
        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("message", "Product saved successfully!");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
    }

    return "redirect:/products";
}

    
    // Delete product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/products";
    }
}
