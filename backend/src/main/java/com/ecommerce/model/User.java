package com.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User entity — maps to 'users' table via Hibernate auto DDL.
 *
 * WHY @JsonIgnore on orders and cart: User ↔ Order is bidirectional. Without
 * @JsonIgnore: User → orders → each Order → user → orders → ... INFINITE LOOP
 * User ↔ Cart is bidirectional. Without @JsonIgnore: User → cart → user → cart
 * → ... INFINITE LOOP Both collections are ignored on the User side — the
 * frontend never needs full order history embedded inside a user object. Orders
 * are fetched separately via /api/orders/user/{id}.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    // Email is used as the login identifier — must be unique
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // Password stored as plain text (no security required per spec)
    @Column(nullable = false)
    private String password;

    // Full shipping address in one field for simplicity
    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    // Role determines access level: CUSTOMER or ADMIN
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private User.Role role = User.Role.CUSTOMER;

    // Timestamp when account was created — uses Java 8 LocalDateTime
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Soft-delete: deactivated accounts remain in DB for order history
    @Column(name = "is_active")
    private Boolean isActive = true;

    /**
     * @JsonIgnore — prevents User → orders → user → ... infinite loop. Orders
     * are loaded separately; they don't need to be embedded in User JSON.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    /**
     * @JsonIgnore — prevents User → cart → user → ... infinite loop. Cart is
     * fetched directly via /api/cart/{userId}, not through User.
     */
    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

    public enum Role {
        CUSTOMER, ADMIN
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public User.Role getRole() {
		return role;
	}

	public void setRole(User.Role role) {
		this.role = role;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public User(Long id, String firstName, String lastName, String email, String password, String shippingAddress,
			Role role, LocalDateTime createdAt, Boolean isActive, List<Order> orders, Cart cart) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.shippingAddress = shippingAddress;
		this.role = role;
		this.createdAt = createdAt;
		this.isActive = isActive;
		this.orders = orders;
		this.cart = cart;
	}

	public User(String firstName, String lastName, String email, String password, String shippingAddress, Role role,
			LocalDateTime createdAt, Boolean isActive, List<Order> orders, Cart cart) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.shippingAddress = shippingAddress;
		this.role = role;
		this.createdAt = createdAt;
		this.isActive = isActive;
		this.orders = orders;
		this.cart = cart;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", password=" + password + ", shippingAddress=" + shippingAddress + ", role=" + role + ", createdAt="
				+ createdAt + ", isActive=" + isActive + ", orders=" + orders + ", cart=" + cart + "]";
	}

	public User() {
		super();
	}
    
    
}
