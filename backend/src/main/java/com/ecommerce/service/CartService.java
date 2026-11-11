package com.ecommerce.service;

import com.ecommerce.dto.CartItemRequest;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * CartService — add/update/remove items from a user's persistent cart.
 * Cart persists across sessions since it's stored in DB.
 */
@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Add a product to the cart, or increase quantity if already present.
     * This prevents duplicate cart items for the same product.
     */
    public Cart addToCart(CartItemRequest request) {
        System.out.println("[INFO] Adding to cart - User: " + request.getUserId()
                + ", Product: " + request.getProductId());

        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + request.getUserId()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));

        // Validate product is still available
        if (!product.getIsActive()) {
            throw new BadRequestException("Product is no longer available: " + product.getName());
        }

        // Check requested qty doesn't exceed stock
        if (request.getQuantity() > product.getQuantity()) {
            throw new BadRequestException("Only " + product.getQuantity()
                    + " units available for: " + product.getName());
        }

        // If product already in cart, increase quantity; otherwise add new item
        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQty = item.getQuantity() + request.getQuantity();

            if (newQty > product.getQuantity()) {
                throw new BadRequestException("Total quantity exceeds stock for: " + product.getName());
            }
            item.setQuantity(newQty);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            cart.getItems().add(newItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    /**
     * Update quantity of a specific cart item.
     * Setting qty to 0 removes the item.
     */
    public Cart updateCartItem(Long cartItemId, Integer quantity) {
        System.out.println("[INFO] Updating cart item ID: " + cartItemId + " to qty: " + quantity);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", cartItemId));

        if (quantity <= 0) {
            // Remove item if quantity drops to zero
            Cart cart = item.getCart();
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
            cart.setUpdatedAt(LocalDateTime.now());
            return cartRepository.save(cart);
        }

        // Validate stock
        if (quantity > item.getProduct().getQuantity()) {
            throw new BadRequestException("Only " + item.getProduct().getQuantity() + " units in stock");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        Cart cart = item.getCart();
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    /**
     * Remove a specific item from the cart entirely.
     */
    public Cart removeFromCart(Long cartItemId) {
        System.out.println("[INFO] Removing cart item ID: " + cartItemId);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", cartItemId));

        Cart cart = item.getCart();
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    /**
     * Get a user's cart with all items.
     */
    @Transactional(readOnly = true)
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));
    }

    /**
     * Clear all items from a cart (used after successful checkout).
     */
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        cartItemRepository.deleteByCartId(cart.getId());
        cart.getItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        System.out.println("[INFO] Cart cleared for user ID: " + userId);
    }

	public CartRepository getCartRepository() {
		return cartRepository;
	}

	public void setCartRepository(CartRepository cartRepository) {
		this.cartRepository = cartRepository;
	}

	public CartItemRepository getCartItemRepository() {
		return cartItemRepository;
	}

	public void setCartItemRepository(CartItemRepository cartItemRepository) {
		this.cartItemRepository = cartItemRepository;
	}

	public ProductRepository getProductRepository() {
		return productRepository;
	}

	public void setProductRepository(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public String toString() {
		return "CartService [cartRepository=" + cartRepository + ", cartItemRepository=" + cartItemRepository
				+ ", productRepository=" + productRepository + "]";
	}

	public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
			ProductRepository productRepository) {
		super();
		this.cartRepository = cartRepository;
		this.cartItemRepository = cartItemRepository;
		this.productRepository = productRepository;
	}

	public CartService() {
		super();
	}
    
	
}
