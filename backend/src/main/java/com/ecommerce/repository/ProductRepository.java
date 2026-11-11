package com.ecommerce.repository;

import com.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ProductRepository — handles all product search and filter queries.
 * Uses Spring Data derived queries and JPQL for complex searches.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Search products by name (case-insensitive partial match)
    List<Product> findByNameContainingIgnoreCaseAndIsActive(String name, Boolean isActive);

    // Filter products by category
    List<Product> findByCategoryIdAndIsActive(Long categoryId, Boolean isActive);

    // Get all active products for storefront
    List<Product> findByIsActive(Boolean isActive);

    // Combined search: name OR category name — used in frontend search bar
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    // Admin: get low stock products (quantity <= threshold)
    List<Product> findByQuantityLessThanEqualAndIsActive(Integer threshold, Boolean isActive);
}
