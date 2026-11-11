package com.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cart entity — one cart per user, persists across sessions.
 *
 * WHY @JsonIgnoreProperties on user: Cart → user → cart → user → ... would loop
 * without this. We include basic user info (id, firstName) for display, but
 * skip 'cart' and 'orders' fields on the User side to break the cycle.
 */
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * WHY FetchType.EAGER: Cart.user was LAZY, which means Hibernate loads a
     * proxy object instead of the real User. After CartService.addToCart()
     * saves and returns, the
     *
     * @Transactional boundary on the service closes the Hibernate session.
     * Jackson then tries to serialize cart.user.firstName — the proxy cannot
     * initialize because there is no open session →
     * LazyInitializationException.
     *
     * FetchType.EAGER forces Hibernate to load the full User row in the same
     * query that fetches the Cart (via a JOIN), so the data is already in
     * memory before the session closes, and Jackson can serialize it without
     * any session.
     *
     * The Cart→User JOIN adds negligible overhead — a cart always has exactly
     * one user, so this is a single extra column group, not a collection.
     */
    @JsonIgnoreProperties({"cart", "orders", "password"})
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // Cart items — serialized EAGER so the full cart loads in one call
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItem> items = new ArrayList<>();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

	

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

	public List<CartItem> getItems() {
		return items;
	}

	public void setItems(List<CartItem> items) {
		this.items = items;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Cart(Long id, User user, List<CartItem> items, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.user = user;
		this.items = items;
		this.updatedAt = updatedAt;
	}

	public Cart(User user, List<CartItem> items, LocalDateTime updatedAt) {
		super();
		this.user = user;
		this.items = items;
		this.updatedAt = updatedAt;
	}

	public Cart() {
		super();
	}

	@Override
	public String toString() {
		return "Cart [id=" + id + ", user=" + user + ", items=" + items + ", updatedAt=" + updatedAt + "]";
	}

	
    
}
