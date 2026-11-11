package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.CartItemRequest;
import com.ecommerce.model.Cart;
import com.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * CartController — REST endpoints for shopping cart operations. Base URL:
 * /api/cart
 *
 * WHY @Transactional on ALL methods (including write operations):
 *
 * Root cause of the "add to cart" error: Cart.user was FetchType.LAZY →
 * Hibernate loads a proxy for User. CartService.addToCart() is @Transactional —
 * when it returns, the service-level transaction commits and the Hibernate
 * session closes. Back in this controller, Spring tries to serialize the
 * returned Cart via Jackson. Jackson accesses cart.user.firstName — but the
 * User is still a proxy with no open session → LazyInitializationException.
 *
 * Two-pronged fix: 1. Cart.user changed to FetchType.EAGER (Cart.java) — User
 * is loaded in the same SQL JOIN as Cart, so it's fully materialized before the
 * session closes. 2. @Transactional on controller methods (this file) — extends
 * the Hibernate session through Jackson serialization as a safety net, so even
 * if any other lazy proxy is touched during serialization it can still be
 * initialized.
 */
@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500",
    "http://127.0.0.1:3000", "http://localhost:5500"})
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * GET /api/cart/{userId} — get user's cart with all items
     */
    @GetMapping("/{userId}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Cart>> getCart(@PathVariable Long userId) {
        System.out.println("[API] GET /cart/" + userId);
        return ResponseEntity.ok(ApiResponse.success("Cart fetched",
                cartService.getCartByUserId(userId)));
    }

    /**
     * POST /api/cart/add — add item to cart (or increment qty if already
     * present)
     */
    @PostMapping("/add")
    @Transactional
    public ResponseEntity<ApiResponse<Cart>> addToCart(
            @Valid @RequestBody CartItemRequest request) {
        System.out.println("[API] POST /cart/add - Product: " + request.getProductId());
        return ResponseEntity.ok(ApiResponse.success("Item added to cart",
                cartService.addToCart(request)));
    }

    /**
     * PUT /api/cart/item/{itemId} — update quantity of a specific item
     */
    @PutMapping("/item/{itemId}")
    @Transactional
    public ResponseEntity<ApiResponse<Cart>> updateItem(
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> body) {
        System.out.println("[API] PUT /cart/item/" + itemId);
        return ResponseEntity.ok(ApiResponse.success("Cart updated",
                cartService.updateCartItem(itemId, body.get("quantity"))));
    }

    /**
     * DELETE /api/cart/item/{itemId} — remove one specific item
     */
    @DeleteMapping("/item/{itemId}")
    @Transactional
    public ResponseEntity<ApiResponse<Cart>> removeItem(@PathVariable Long itemId) {
        System.out.println("[API] DELETE /cart/item/" + itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart",
                cartService.removeFromCart(itemId)));
    }

    /**
     * DELETE /api/cart/{userId}/clear — empty the entire cart
     */
    @DeleteMapping("/{userId}/clear")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable Long userId) {
        System.out.println("[API] DELETE /cart/" + userId + "/clear");
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared"));
    }
}
