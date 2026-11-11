package com.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * CartItemRequest DTO — used to add or update a cart item.
 * userId identifies whose cart to update.
 */

public class CartItemRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "CartItemRequest [userId=" + userId + ", productId=" + productId + ", quantity=" + quantity + "]";
	}

	public CartItemRequest(@NotNull(message = "User ID is required") Long userId,
			@NotNull(message = "Product ID is required") Long productId,
			@NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1") Integer quantity) {
		super();
		this.userId = userId;
		this.productId = productId;
		this.quantity = quantity;
	}

	public CartItemRequest(@NotNull(message = "Product ID is required") Long productId,
			@NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1") Integer quantity) {
		super();
		this.productId = productId;
		this.quantity = quantity;
	}

	public CartItemRequest() {
		super();
	}
    
    
}
