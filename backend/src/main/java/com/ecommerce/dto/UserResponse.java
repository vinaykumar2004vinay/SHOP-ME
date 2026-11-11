package com.ecommerce.dto;

import com.ecommerce.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * UserResponse DTO — safe user data returned to frontend.
 * Password is intentionally excluded — never send passwords in responses.
 */

public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String shippingAddress;
    private String role;
    private LocalDateTime createdAt;
    private Boolean isActive;

    // Constructor from User entity — maps only safe fields
    public UserResponse(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.shippingAddress = user.getShippingAddress();
        this.role = user.getRole().name();
        this.createdAt = user.getCreatedAt();
        this.isActive = user.getIsActive();
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

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
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

	@Override
	public String toString() {
		return "UserResponse [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", shippingAddress=" + shippingAddress + ", role=" + role + ", createdAt=" + createdAt + ", isActive="
				+ isActive + "]";
	}

	public UserResponse() {
		super();
	}

	public UserResponse(Long id, String firstName, String lastName, String email, String shippingAddress, String role,
			LocalDateTime createdAt, Boolean isActive) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.shippingAddress = shippingAddress;
		this.role = role;
		this.createdAt = createdAt;
		this.isActive = isActive;
	}
    
    
}
