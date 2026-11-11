package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.ProductRequest;
import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProductController — product catalog REST endpoints. Base URL: /api/products
 *
 * WHY @Transactional on all methods: Product has ManyToOne → Category (EAGER).
 * Even with EAGER fetching, keeping the session open through serialization
 * ensures that any nested lazy proxy (e.g. Category.products if it were ever
 * accessed) won't throw. This is the defensive approach — cheap to add,
 * prevents hard-to-debug LazyInitializationExceptions in the future.
 *
 * WHY /search and /admin are declared before /{id}: Spring MVC resolves
 * literals before path variables. Being explicit prevents "search" or "admin"
 * from being tried as a Long ID.
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500",
    "http://127.0.0.1:3000", "http://localhost:5500"})
public class ProductController {

    @Autowired
    private ProductService productService;

    // ── Literal routes first ───────────────────────────────────────
    /**
     * GET /api/products — all active products for the storefront
     */
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        System.out.println("[API] GET /products");
        return ResponseEntity.ok(ApiResponse.success("Products fetched",
                productService.getAllActiveProducts()));
    }

    /**
     * GET /api/products/admin — all products including inactive (admin)
     */
    @GetMapping("/admin")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Product>>> getAllProductsAdmin() {
        System.out.println("[API] GET /products/admin");
        return ResponseEntity.ok(ApiResponse.success("All products fetched",
                productService.getAllProductsForAdmin()));
    }

    /**
     * GET /api/products/search?keyword=... — search by name or category
     */
    @GetMapping("/search")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Product>>> search(
            @RequestParam(required = false, defaultValue = "") String keyword) {
        System.out.println("[API] GET /products/search?keyword=" + keyword);
        return ResponseEntity.ok(ApiResponse.success("Search results",
                productService.searchProducts(keyword)));
    }

    /**
     * GET /api/products/category/{categoryId} — filter by category
     */
    @GetMapping("/category/{categoryId}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Product>>> getByCategory(
            @PathVariable Long categoryId) {
        System.out.println("[API] GET /products/category/" + categoryId);
        return ResponseEntity.ok(ApiResponse.success("Products by category",
                productService.getProductsByCategory(categoryId)));
    }

    // ── Variable route /{id} last ──────────────────────────────────
    /**
     * GET /api/products/{id} — single product
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable Long id) {
        System.out.println("[API] GET /products/" + id);
        return ResponseEntity.ok(ApiResponse.success("Product fetched",
                productService.getProductById(id)));
    }

    /**
     * POST /api/products — admin: add new product
     */
    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<Product>> addProduct(
            @Valid @RequestBody ProductRequest request) {
        System.out.println("[API] POST /products - " + request.getName());
        return ResponseEntity.ok(ApiResponse.success("Product added successfully",
                productService.addProduct(request)));
    }

    /**
     * PUT /api/products/{id} — admin: edit product
     */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        System.out.println("[API] PUT /products/" + id);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully",
                productService.updateProduct(id, request)));
    }

    /**
     * DELETE /api/products/{id} — admin: soft-delete product
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        System.out.println("[API] DELETE /products/" + id);
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }
}
