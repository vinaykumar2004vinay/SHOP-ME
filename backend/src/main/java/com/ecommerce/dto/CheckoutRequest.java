package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * CheckoutRequest DTO — collects all info needed to place an order.
 * Submitted by user at the final checkout step.
 */

public class CheckoutRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // e.g., "CREDIT_CARD", "DEBIT_CARD", "UPI", "COD"

    @NotBlank(message = "Delivery option is required")
    private String deliveryOption; // e.g., "STANDARD", "EXPRESS", "OVERNIGHT"

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	@Override
	public String toString() {
		return "CheckoutRequest [userId=" + userId + ", shippingAddress=" + shippingAddress + ", paymentMethod="
				+ paymentMethod + ", deliveryOption=" + deliveryOption + "]";
	}

	public CheckoutRequest(@NotNull(message = "User ID is required") Long userId,
			@NotBlank(message = "Shipping address is required") String shippingAddress,
			@NotBlank(message = "Payment method is required") String paymentMethod,
			@NotBlank(message = "Delivery option is required") String deliveryOption) {
		super();
		this.userId = userId;
		this.shippingAddress = shippingAddress;
		this.paymentMethod = paymentMethod;
		this.deliveryOption = deliveryOption;
	}

	public CheckoutRequest(@NotBlank(message = "Shipping address is required") String shippingAddress,
			@NotBlank(message = "Payment method is required") String paymentMethod,
			@NotBlank(message = "Delivery option is required") String deliveryOption) {
		super();
		this.shippingAddress = shippingAddress;
		this.paymentMethod = paymentMethod;
		this.deliveryOption = deliveryOption;
	}

	public CheckoutRequest() {
		super();
	}
    
    
}
