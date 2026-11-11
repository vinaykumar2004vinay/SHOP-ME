package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.RegisterRequest;
import com.ecommerce.dto.UpdateProfileRequest;
import com.ecommerce.dto.UserResponse;
import com.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UserController — registration, login, and profile management. Base URL:
 * /api/users
 *
 * KEY CHANGE: updateProfile() now uses UpdateProfileRequest (password optional)
 * instead of RegisterRequest (password @NotBlank — mandatory).
 *
 * Original bug: PUT /api/users/{id} used RegisterRequest which has @NotBlank on
 * password. When the frontend sent a profile update without changing the
 * password (password field null/absent), Bean Validation rejected it with:
 * "Validation failed: {password=Password is required}"
 *
 * Fix: Introduced UpdateProfileRequest with password @Size(min=6) only — null
 * is valid. The service skips the password update when the field is blank/null.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500",
    "http://127.0.0.1:3000", "http://localhost:5500"})
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * POST /api/users/register — new account (password mandatory)
     */
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        System.out.println("[API] POST /register - " + request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(
                "Registration successful! Welcome to ShopEase.",
                userService.register(request)));
    }

    /**
     * POST /api/users/login
     */
    @PostMapping("/login")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<UserResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        System.out.println("[API] POST /login - " + request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(
                "Login successful! Welcome back.",
                userService.login(request)));
    }

    /**
     * GET /api/users/{id} — fetch profile
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        System.out.println("[API] GET /users/" + id);
        return ResponseEntity.ok(ApiResponse.success("User fetched",
                userService.getUserById(id)));
    }

    /**
     * PUT /api/users/{id} — update profile. Uses UpdateProfileRequest where
     * password is OPTIONAL (null = keep existing).
     */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request) {
        System.out.println("[API] PUT /users/" + id);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully",
                userService.updateProfile(id, request)));
    }

    /**
     * GET /api/users — admin: all users
     */
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        System.out.println("[API] GET /users - admin");
        return ResponseEntity.ok(ApiResponse.success("Users fetched",
                userService.getAllUsers()));
    }

    /**
     * PATCH /api/users/{id}/toggle-status — admin: activate/deactivate
     */
    @PatchMapping("/{id}/toggle-status")
    @Transactional
    public ResponseEntity<ApiResponse<UserResponse>> toggleStatus(@PathVariable Long id) {
        System.out.println("[API] PATCH /users/" + id + "/toggle-status");
        return ResponseEntity.ok(ApiResponse.success("User status updated",
                userService.toggleUserStatus(id)));
    }
}
