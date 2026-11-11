package com.ecommerce.repository;

import com.ecommerce.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * CartRepository — each user has exactly one cart (one-to-one).
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Fetch the cart belonging to a specific user
    Optional<Cart> findByUserId(Long userId);
}
