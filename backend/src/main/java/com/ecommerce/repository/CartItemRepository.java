package com.ecommerce.repository;

import com.ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * CartItemRepository — manages individual line items inside a cart.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Find a specific product already in a specific cart — used to update qty instead of duplicate
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    // Remove all items from a cart (used during checkout)
    void deleteByCartId(Long cartId);
}
