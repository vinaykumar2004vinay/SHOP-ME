package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.model.Category;
import com.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * CategoryController — product category endpoints. Base URL: /api/categories
 *
 * @Transactional on all methods keeps the Hibernate session open through
 * Jackson serialization — preventing any lazy proxy error if Hibernate decides
 * to proxy a related entity.
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500",
    "http://127.0.0.1:3000", "http://localhost:5500"})
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * GET /api/categories — all categories
     */
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Category>>> getAll() {
        System.out.println("[API] GET /categories");
        return ResponseEntity.ok(ApiResponse.success("Categories fetched",
                categoryService.getAllCategories()));
    }

    /**
     * GET /api/categories/{id}
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Category>> getById(@PathVariable Long id) {
        System.out.println("[API] GET /categories/" + id);
        return ResponseEntity.ok(ApiResponse.success("Category fetched",
                categoryService.getCategoryById(id)));
    }

    /**
     * POST /api/categories — admin: add category
     */
    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<Category>> add(@RequestBody Map<String, String> body) {
        System.out.println("[API] POST /categories - " + body.get("name"));
        Category category = categoryService.addCategory(
                body.getOrDefault("name", ""),
                body.getOrDefault("description", ""),
                body.getOrDefault("icon", ""));
        return ResponseEntity.ok(ApiResponse.success("Category added", category));
    }

    /**
     * PUT /api/categories/{id} — admin: update category
     */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Category>> update(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        System.out.println("[API] PUT /categories/" + id);
        Category category = categoryService.updateCategory(
                id,
                body.getOrDefault("name", ""),
                body.getOrDefault("description", ""),
                body.getOrDefault("icon", ""));
        return ResponseEntity.ok(ApiResponse.success("Category updated", category));
    }

    /**
     * DELETE /api/categories/{id} — admin: delete category
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        System.out.println("[API] DELETE /categories/" + id);
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted"));
    }
}
