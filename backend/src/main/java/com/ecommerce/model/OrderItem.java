package com.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * OrderItem — a snapshot of a product at the time of purchase.
 *
 * WHY @JsonIgnore on order: OrderItem has ManyToOne → Order, and Order has
 * OneToMany → items. Serializing OrderItem → order → items → orderItem → order
 * → ... loops. The 'order' back-reference is ignored; OrderItems are always
 * accessed through their parent Order, never independently.
 *
 * WHY @JsonIgnoreProperties on product: Product → category → products (if not
 * @JsonIgnored on category side). Category.products is already @JsonIgnored,
 * but being explicit here on the product's category avoids any edge cases.
 */
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @JsonIgnore — breaks OrderItem → Order → OrderItem loop. The order
     * reference is navigation-only; it's never serialized outward.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Product info needed by frontend (name, image, price)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    // Price captured at order time — historical record, never changes
    @Column(name = "price_at_purchase", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtPurchase;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPriceAtPurchase() {
		return priceAtPurchase;
	}

	public void setPriceAtPurchase(BigDecimal priceAtPurchase) {
		this.priceAtPurchase = priceAtPurchase;
	}

	@Override
	public String toString() {
		return "OrderItem [id=" + id + ", order=" + order + ", product=" + product + ", quantity=" + quantity
				+ ", priceAtPurchase=" + priceAtPurchase + "]";
	}

	public OrderItem(Long id, Order order, Product product, Integer quantity, BigDecimal priceAtPurchase) {
		super();
		this.id = id;
		this.order = order;
		this.product = product;
		this.quantity = quantity;
		this.priceAtPurchase = priceAtPurchase;
	}

	public OrderItem(Order order, Product product, Integer quantity, BigDecimal priceAtPurchase) {
		super();
		this.order = order;
		this.product = product;
		this.quantity = quantity;
		this.priceAtPurchase = priceAtPurchase;
	}

	public OrderItem() {
		super();
	}
    
    
}
