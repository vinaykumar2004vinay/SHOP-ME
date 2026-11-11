package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.model.Order;
import com.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * OrderController — checkout and order management endpoints. Base URL:
 * /api/orders
 *
 * WHY @Transactional on every method: Order.user is ManyToOne. Even though we
 * changed it to EAGER, the
 *
 * @Transactional annotation here ensures the Hibernate session is kept open all
 * the way through Jackson serialization — a general safety net so any future
 * LAZY field addition won't silently cause proxy errors.
 *
 * WHY literal routes come before /{orderId}: Spring MVC resolves literal path
 * segments (/stats, /checkout) before path variables, but being explicit
 * prevents IDE warnings and documents the routing intent clearly.
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500",
    "http://127.0.0.1:3000", "http://localhost:5500"})
public class OrderController {

    @Autowired
    private OrderService orderService;

    // ── Literal routes FIRST (before /{orderId}) ─────────────────
    /**
     * POST /api/orders/checkout — convert cart into a placed order
     */
    @PostMapping("/checkout")
    @Transactional
    public ResponseEntity<ApiResponse<Order>> checkout(
            @Valid @RequestBody CheckoutRequest request) {
        System.out.println("[API] POST /orders/checkout - User: " + request.getUserId());
        return ResponseEntity.ok(ApiResponse.success(
                "Order placed successfully! Check your email for confirmation.",
                orderService.checkout(request)));
    }

    /**
     * GET /api/orders/stats — admin dashboard counts per status
     */
    @GetMapping("/stats")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Long>>> getStats() {
        System.out.println("[API] GET /orders/stats");
        return ResponseEntity.ok(ApiResponse.success("Stats fetched",
                orderService.getOrderStats()));
    }

    /**
     * GET /api/orders — admin: all orders, newest first
     */
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        System.out.println("[API] GET /orders - admin");
        return ResponseEntity.ok(ApiResponse.success("All orders fetched",
                orderService.getAllOrders()));
    }

    /**
     * GET /api/orders/user/{userId} — user's personal order history
     */
    @GetMapping("/user/{userId}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Order>>> getUserOrders(
            @PathVariable Long userId) {
        System.out.println("[API] GET /orders/user/" + userId);
        return ResponseEntity.ok(ApiResponse.success("Orders fetched",
                orderService.getUserOrders(userId)));
    }

    // ── Variable routes AFTER literals ───────────────────────────
    /**
     * GET /api/orders/{orderId} — single order with items
     */
    @GetMapping("/{orderId}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable Long orderId) {
        System.out.println("[API] GET /orders/" + orderId);
        return ResponseEntity.ok(ApiResponse.success("Order fetched",
                orderService.getOrderById(orderId)));
    }

    /**
     * PATCH /api/orders/{orderId}/status — admin: advance order status
     */
    @PatchMapping("/{orderId}/status")
    @Transactional
    public ResponseEntity<ApiResponse<Order>> updateStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> body) {
        System.out.println("[API] PATCH /orders/" + orderId + "/status → " + body.get("status"));
        return ResponseEntity.ok(ApiResponse.success("Order status updated",
                orderService.updateOrderStatus(orderId, body.get("status"))));
    }

    /**
     * POST /api/orders/{orderId}/cancel — user or admin cancels an order
     */
    @PostMapping("/{orderId}/cancel")
    @Transactional
    public ResponseEntity<ApiResponse<Order>> cancelOrder(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> body) {
        System.out.println("[API] POST /orders/" + orderId + "/cancel");
        boolean isAdmin = Boolean.parseBoolean(body.getOrDefault("isAdmin", "false"));
        String reason = body.getOrDefault("reason", "No reason provided");
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully",
                orderService.cancelOrder(orderId, reason, isAdmin)));
    }
}
