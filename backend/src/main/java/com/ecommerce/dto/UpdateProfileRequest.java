package com.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * UpdateProfileRequest — used for PUT /api/users/{id} (profile update).
 *
 * WHY a separate DTO from RegisterRequest:
 *   RegisterRequest has @NotBlank on password — mandatory during registration.
 *   But when updating a profile the user may not want to change their password,
 *   so the password field should be optional (null = keep existing password).
 *   Reusing RegisterRequest caused: "Validation failed: {password=Password is required}"
 *   whenever the frontend sent a profile update without a new password.
 *
 *   Solution: split into two DTOs with different validation rules.
 *   - RegisterRequest  → password @NotBlank  (registration — password always required)
 *   - UpdateProfileRequest → password @Size only, nullable (update — password optional)
 */

public class UpdateProfileRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be under 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be under 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Password is OPTIONAL on update.
     * If null or blank → keep existing password unchanged.
     * If provided      → must be at least 6 characters.
     */
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

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

	@Override
	public String toString() {
		return "UpdateProfileRequest [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", password=" + password + ", shippingAddress=" + shippingAddress + "]";
	}

	public UpdateProfileRequest(
			@NotBlank(message = "First name is required") @Size(max = 50, message = "First name must be under 50 characters") String firstName,
			@NotBlank(message = "Last name is required") @Size(max = 50, message = "Last name must be under 50 characters") String lastName,
			@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
			@Size(min = 6, message = "Password must be at least 6 characters") String password,
			@NotBlank(message = "Shipping address is required") String shippingAddress) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.shippingAddress = shippingAddress;
	}

	public UpdateProfileRequest() {
		super();
	}
    
    
}
