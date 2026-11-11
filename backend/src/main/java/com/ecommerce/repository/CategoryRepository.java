package com.ecommerce.repository;

import com.ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * CategoryRepository — simple CRUD for product categories.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Prevent duplicate category names
    boolean existsByNameIgnoreCase(String name);

    Optional<Category> findByNameIgnoreCase(String name);
}
