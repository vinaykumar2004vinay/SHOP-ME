package com.ecommerce.service;

import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.*;
import com.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * OrderService — manages the full order lifecycle.
 * Uses Java 8 LocalDateTime for all timestamps and Duration for processing time.
 *
 * Flow: Cart → Checkout → PENDING → PROCESSING → SHIPPED → DELIVERED
 *       (or CANCELLED at any stage before SHIPPED)
 */
@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CartService cartService;

    /**
     * Checkout — convert cart to a placed order.
     * Steps: validate cart → deduct stock → create order → clear cart → send email.
     * All in one @Transactional — rolls back fully if anything fails.
     */
    public Order checkout(CheckoutRequest request) {
        System.out.println("[INFO] Checkout initiated for user ID: " + request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new BadRequestException("No cart found for user"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Your cart is empty. Add items before checkout.");
        }

        // Build the Order and its line items
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setDeliveryOption(request.getDeliveryOption());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setPlacedAt(LocalDateTime.now()); // Java 8 LocalDateTime

        BigDecimal total = BigDecimal.ZERO;

        // Convert each cart item to an order item and deduct stock
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            // Final stock validation at checkout time
            if (cartItem.getQuantity() > product.getQuantity()) {
                throw new BadRequestException("Insufficient stock for: " + product.getName()
                        + ". Available: " + product.getQuantity());
            }

            // Create immutable order item (price snapshotted at purchase time)
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice()); // Snapshot price

            order.getItems().add(orderItem);

            // Accumulate total using BigDecimal for precision
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            // Deduct stock
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);

        // Clear the cart after successful order placement
        cartService.clearCart(request.getUserId());

        // Send order confirmation email (non-blocking — failure won't break order)
        emailService.sendOrderConfirmation(savedOrder);

        System.out.println("[INFO] Order #" + savedOrder.getId() + " placed successfully. Total: ₹" + total);
        return savedOrder;
    }

    /**
     * Get all orders for a specific user — their order history.
     */
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByPlacedAtDesc(userId);
    }

    /**
     * Get a single order by ID.
     */
    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    }

    /**
     * Admin: get all orders.
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByPlacedAtDesc();
    }

    /**
     * Admin: update order status.
     * Uses Java 8 LocalDateTime to timestamp status transitions.
     */
    public Order updateOrderStatus(Long orderId, String status) {
        System.out.println("[INFO] Updating order #" + orderId + " status to: " + status);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        Order.OrderStatus newStatus;
        try {
            newStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid order status: " + status);
        }

        // Cannot move back in the pipeline
        if (order.getStatus() == Order.OrderStatus.DELIVERED
                || order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot change status of a " + order.getStatus() + " order");
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        // Track exact timestamps for shipped and delivered milestones
        if (newStatus == Order.OrderStatus.SHIPPED) {
            order.setShippedAt(LocalDateTime.now());
        } else if (newStatus == Order.OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());

            // Java 8 Duration — calculate total processing time for logs
            Duration processingTime = Duration.between(order.getPlacedAt(), LocalDateTime.now());
            System.out.println("[INFO] Order #" + orderId + " delivered. Processing time: "
                    + processingTime.toDays() + " days, "
                    + (processingTime.toHours() % 24) + " hours");
        }

        return orderRepository.save(order);
    }

    /**
     * Cancel an order. User can cancel only PENDING or PROCESSING orders.
     * Admin can cancel any non-delivered order.
     * Restores stock and sends cancellation email.
     */
    public Order cancelOrder(Long orderId, String reason, boolean isAdmin) {
        System.out.println("[INFO] Cancelling order #" + orderId + " | Reason: " + reason);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        // Users can only cancel before shipping; admins have broader authority
        if (!isAdmin && order.getStatus() == Order.OrderStatus.SHIPPED) {
            throw new BadRequestException("Cannot cancel an order that has already been shipped.");
        }

        if (order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot cancel a delivered order.");
        }

        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new BadRequestException("Order is already cancelled.");
        }

        // Restore stock for all items in the cancelled order
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
            System.out.println("[INFO] Restored " + item.getQuantity()
                    + " units of: " + product.getName());
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancellationReason(reason);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Notify user via email
        emailService.sendCancellationEmail(savedOrder);

        return savedOrder;
    }

    /**
     * Admin dashboard stats — count orders by status.
     */
    @Transactional(readOnly = true)
    public java.util.Map<String, Long> getOrderStats() {
        java.util.Map<String, Long> stats = new java.util.LinkedHashMap<>();
        stats.put("total", orderRepository.count());
        stats.put("pending", orderRepository.countByStatus(Order.OrderStatus.PENDING));
        stats.put("processing", orderRepository.countByStatus(Order.OrderStatus.PROCESSING));
        stats.put("shipped", orderRepository.countByStatus(Order.OrderStatus.SHIPPED));
        stats.put("delivered", orderRepository.countByStatus(Order.OrderStatus.DELIVERED));
        stats.put("cancelled", orderRepository.countByStatus(Order.OrderStatus.CANCELLED));
        return stats;
    }
}
