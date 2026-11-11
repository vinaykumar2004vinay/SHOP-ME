package com.ecommerce.service;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ProductService — CRUD for products, search, and stock management.
 * Re-uses buildProduct() for both create and update to avoid duplication.
 */
@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Add a new product. Admin only.
     */
    public Product addProduct(ProductRequest request) {
        System.out.println("[INFO] Adding product: " + request.getName());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        Product product = new Product();
        mapRequestToProduct(request, product, category);

        Product saved = productRepository.save(product);
        System.out.println("[INFO] Product saved with ID: " + saved.getId());
        return saved;
    }

    /**
     * Update existing product. Admin only.
     * Re-uses mapRequestToProduct() — same logic as add.
     */
    public Product updateProduct(Long productId, ProductRequest request) {
        System.out.println("[INFO] Updating product ID: " + productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        mapRequestToProduct(request, product, category);
        product.setUpdatedAt(LocalDateTime.now()); // Track when it was last modified

        return productRepository.save(product);
    }

    /**
     * Soft-delete a product (set isActive = false).
     * Products with orders can't be hard-deleted — they'd break order history.
     */
    public void deleteProduct(Long productId) {
        System.out.println("[INFO] Soft-deleting product ID: " + productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        product.setIsActive(false);
        productRepository.save(product);
    }

    /**
     * Get single product by ID.
     */
    @Transactional(readOnly = true)
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
    }

    /**
     * Get all active products for storefront.
     */
    @Transactional(readOnly = true)
    public List<Product> getAllActiveProducts() {
        return productRepository.findByIsActive(true);
    }

    /**
     * Admin: get all products including inactive.
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProductsForAdmin() {
        return productRepository.findAll();
    }

    /**
     * Search products by keyword — matches name or category name.
     * Used by the frontend search bar.
     */
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String keyword) {
        System.out.println("[INFO] Searching products with keyword: " + keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findByIsActive(true);
        }
        return productRepository.searchByKeyword(keyword.trim());
    }

    /**
     * Filter products by category.
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryIdAndIsActive(categoryId, true);
    }

    /**
     * Reusable mapper: copies ProductRequest fields onto a Product entity.
     * Used by both addProduct and updateProduct to avoid code duplication.
     */
    private void mapRequestToProduct(ProductRequest request, Product product, Category category) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        product.setIsActive(true);
    }
}
