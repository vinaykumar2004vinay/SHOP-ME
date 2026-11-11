package com.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity — represents a placed purchase. Status flows: PENDING →
 * PROCESSING → SHIPPED → DELIVERED (or CANCELLED)
 *
 * WHY @JsonIgnoreProperties on user: Order has ManyToOne → User, and User has
 * OneToMany → orders. Serializing Order → user → orders → order → user → ...
 * would loop.
 *
 * @JsonIgnoreProperties("orders") tells Jackson: when serializing the User
 * object *inside* an Order, skip the 'orders' field only. This way the frontend
 * still gets user name/email with each order (useful in admin panel) but
 * without the infinite loop.
 *
 * WHY @JsonIgnoreProperties on items' order back-reference: OrderItem has a
 * back-reference to Order. Handled in OrderItem.java.
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @JsonIgnoreProperties — include user name/email in order response (needed
     * in admin panel) but block the circular 'orders','cart','password' fields.
     *
     * WHY FetchType.EAGER: Order.user was LAZY — same proxy problem as
     * Cart.user. When OrderService methods return an Order, the session closes
     * and Jackson can't access user.firstName. EAGER loads User in the same
     * JOIN, fully materialized.
     */
    @JsonIgnoreProperties({"orders", "cart", "password"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Order line items serialized inline (customer needs item details)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "delivery_option", length = 50)
    private String deliveryOption;

    // Order lifecycle status — drives the status timeline on frontend
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    // Java 8 LocalDateTime: all timestamps use the Java 8 date API
    @Column(name = "placed_at")
    private LocalDateTime placedAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    public enum OrderStatus {
        PENDING,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getDeliveryOption() {
		return deliveryOption;
	}

	public void setDeliveryOption(String deliveryOption) {
		this.deliveryOption = deliveryOption;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public String getCancellationReason() {
		return cancellationReason;
	}

	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}

	public LocalDateTime getPlacedAt() {
		return placedAt;
	}

	public void setPlacedAt(LocalDateTime placedAt) {
		this.placedAt = placedAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public LocalDateTime getShippedAt() {
		return shippedAt;
	}

	public void setShippedAt(LocalDateTime shippedAt) {
		this.shippedAt = shippedAt;
	}

	public LocalDateTime getDeliveredAt() {
		return deliveredAt;
	}

	public void setDeliveredAt(LocalDateTime deliveredAt) {
		this.deliveredAt = deliveredAt;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", user=" + user + ", items=" + items + ", totalAmount=" + totalAmount
				+ ", shippingAddress=" + shippingAddress + ", paymentMethod=" + paymentMethod + ", deliveryOption="
				+ deliveryOption + ", status=" + status + ", cancellationReason=" + cancellationReason + ", placedAt="
				+ placedAt + ", updatedAt=" + updatedAt + ", shippedAt=" + shippedAt + ", deliveredAt=" + deliveredAt
				+ "]";
	}

	public Order(User user, List<OrderItem> items, BigDecimal totalAmount, String shippingAddress, String paymentMethod,
			String deliveryOption, OrderStatus status, String cancellationReason, LocalDateTime placedAt,
			LocalDateTime updatedAt, LocalDateTime shippedAt, LocalDateTime deliveredAt) {
		super();
		this.user = user;
		this.items = items;
		this.totalAmount = totalAmount;
		this.shippingAddress = shippingAddress;
		this.paymentMethod = paymentMethod;
		this.deliveryOption = deliveryOption;
		this.status = status;
		this.cancellationReason = cancellationReason;
		this.placedAt = placedAt;
		this.updatedAt = updatedAt;
		this.shippedAt = shippedAt;
		this.deliveredAt = deliveredAt;
	}

	public Order(Long id, User user, List<OrderItem> items, BigDecimal totalAmount, String shippingAddress,
			String paymentMethod, String deliveryOption, OrderStatus status, String cancellationReason,
			LocalDateTime placedAt, LocalDateTime updatedAt, LocalDateTime shippedAt, LocalDateTime deliveredAt) {
		super();
		this.id = id;
		this.user = user;
		this.items = items;
		this.totalAmount = totalAmount;
		this.shippingAddress = shippingAddress;
		this.paymentMethod = paymentMethod;
		this.deliveryOption = deliveryOption;
		this.status = status;
		this.cancellationReason = cancellationReason;
		this.placedAt = placedAt;
		this.updatedAt = updatedAt;
		this.shippedAt = shippedAt;
		this.deliveredAt = deliveredAt;
	}

	public Order() {
		super();
	}
    
    
    
}
