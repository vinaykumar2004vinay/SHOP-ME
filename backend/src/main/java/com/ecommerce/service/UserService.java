package com.ecommerce.service;

import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.RegisterRequest;
import com.ecommerce.dto.UpdateProfileRequest;
import com.ecommerce.dto.UserResponse;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Cart;
import com.ecommerce.model.User;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserService — handles registration, login, and profile management.
 *
 * Key design decisions: - register() uses RegisterRequest (password @NotBlank —
 * required) - updateProfile() uses UpdateProfileRequest (password @Size only —
 * optional) - All methods return UserResponse DTO, never the raw User entity →
 * no risk of serializing lazy proxies or leaking the password field
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    /**
     * Register a new user. Creates user + empty cart atomically in a single
     * transaction.
     */
    public UserResponse register(RegisterRequest request) {
        System.out.println("[INFO] Attempting registration for: " + request.getEmail());

        // Prevent duplicate email registrations
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(request.getPassword());
        user.setShippingAddress(request.getShippingAddress());
        user.setRole(User.Role.CUSTOMER);

        User savedUser = userRepository.save(user);

        // Auto-create an empty cart for every new user
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        System.out.println("[INFO] User registered with ID: " + savedUser.getId());
        return new UserResponse(savedUser);
    }

    /**
     * Login — validates email + password combination. Returns UserResponse
     * (safe DTO, no password, no lazy proxies).
     */
    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest request) {
        System.out.println("[INFO] Login attempt for: " + request.getEmail());

        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!user.getIsActive()) {
            throw new BadRequestException("Your account has been deactivated. Please contact support.");
        }

        if (!user.getPassword().equals(request.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        System.out.println("[INFO] Login successful: " + user.getEmail() + " | Role: " + user.getRole());
        return new UserResponse(user);
    }

    /**
     * Get user profile by ID.
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return new UserResponse(user);
    }

    /**
     * Update profile — uses UpdateProfileRequest which has password as
     * OPTIONAL.
     *
     * WHY UpdateProfileRequest instead of RegisterRequest: RegisterRequest
     * marks password as @NotBlank (mandatory). On a profile update the user may
     * not want to change password, so the frontend sends null/empty. Using
     * RegisterRequest caused "Validation failed: {password=Password is
     * required}" every time. UpdateProfileRequest.password has only
     * @Size(min=6) — null is accepted.
     */
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        System.out.println("[INFO] Updating profile for user ID: " + userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        // Email uniqueness check — only needed if the email is actually changing
        String newEmail = request.getEmail().toLowerCase().trim();
        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new BadRequestException("Email already in use by another account");
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(newEmail);
        user.setShippingAddress(request.getShippingAddress());

        // Only update password if a new one was actually provided
        String newPassword = request.getPassword();
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(newPassword);
            System.out.println("[INFO] Password updated for user ID: " + userId);
        }

        return new UserResponse(userRepository.save(user));
    }

    /**
     * Admin: get all users.
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Admin: toggle user active/inactive (soft delete).
     */
    public UserResponse toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        user.setIsActive(!user.getIsActive());
        System.out.println("[INFO] User " + userId + " active = " + user.getIsActive());
        return new UserResponse(userRepository.save(user));
    }
}
