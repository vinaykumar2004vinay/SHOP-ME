package com.ecommerce.repository;

import com.ecommerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * OrderRepository — query orders by user and status.
 * Spring Data handles SQL generation from method names.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // User's personal order history — most recent first
    List<Order> findByUserIdOrderByPlacedAtDesc(Long userId);

    // Admin: filter orders by status (e.g., all PENDING orders)
    List<Order> findByStatusOrderByPlacedAtDesc(Order.OrderStatus status);

    // Admin: all orders, newest first
    List<Order> findAllByOrderByPlacedAtDesc();

    // Count orders per status — used in admin dashboard stats
    long countByStatus(Order.OrderStatus status);
}
