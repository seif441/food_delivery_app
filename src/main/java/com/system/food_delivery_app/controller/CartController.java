package com.system.food_delivery_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.system.food_delivery_app.model.Cart;
import com.system.food_delivery_app.service.CartService;

/**
 * FIXED: Removed @CrossOrigin - now handled globally in WebConfig
 */
@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * Get or create cart for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCartByUser(@PathVariable Long userId) {
        try {
            if (userId == null) {
                return ResponseEntity.badRequest()
                    .body("User ID cannot be null");
            }
            
            Cart cart = cartService.getOrCreateCart(userId);
            return ResponseEntity.ok(cart);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body("Invalid request: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Error getting cart for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to load cart: " + e.getMessage());
        }
    }

    /**
     * Add item to cart
     */
    @PostMapping("/{cartId}/items")
    public ResponseEntity<?> addItemToCart(
            @PathVariable Long cartId, 
            @RequestParam Long productId, 
            @RequestParam int quantity) {
        
        try {
            if (cartId == null || productId == null) {
                return ResponseEntity.badRequest()
                    .body("Cart ID and Product ID are required");
            }
            
            if (quantity <= 0) {
                return ResponseEntity.badRequest()
                    .body("Quantity must be positive");
            }
            
            System.out.println("Adding to cart - CartID: " + cartId + 
                             ", ProductID: " + productId + 
                             ", Quantity: " + quantity);
            
            Cart updatedCart = cartService.addItemToCart(cartId, productId, quantity);
            return ResponseEntity.ok(updatedCart);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body("Invalid request: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Error adding item to cart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to add item: " + e.getMessage());
        }
    }

    /**
     * Update item quantity
     */
    @PutMapping("/{cartId}/items")
    public ResponseEntity<?> updateItemQuantity(
            @PathVariable Long cartId, 
            @RequestParam Long productId, 
            @RequestParam int quantity) {
        
        try {
            if (cartId == null || productId == null) {
                return ResponseEntity.badRequest()
                    .body("Cart ID and Product ID are required");
            }
            
            System.out.println("Updating cart item - CartID: " + cartId + 
                             ", ProductID: " + productId + 
                             ", New Quantity: " + quantity);
            
            Cart updatedCart = cartService.updateItemQuantity(cartId, productId, quantity);
            return ResponseEntity.ok(updatedCart);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body("Invalid request: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Error updating cart item: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to update item: " + e.getMessage());
        }
    }

    /**
     * Remove item from cart
     */
    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<?> removeItemFromCart(
            @PathVariable Long cartId, 
            @RequestParam Long productId) {
        
        try {
            if (cartId == null || productId == null) {
                return ResponseEntity.badRequest()
                    .body("Cart ID and Product ID are required");
            }
            
            System.out.println("Removing from cart - CartID: " + cartId + 
                             ", ProductID: " + productId);
            
            Cart updatedCart = cartService.removeItemFromCart(cartId, productId);
            return ResponseEntity.ok(updatedCart);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body("Invalid request: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Error removing cart item: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to remove item: " + e.getMessage());
        }
    }

    /**
     * Clear entire cart
     */
    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> clearCart(@PathVariable Long cartId) {
        try {
            if (cartId == null) {
                return ResponseEntity.badRequest()
                    .body("Cart ID is required");
            }
            
            System.out.println("Clearing cart - CartID: " + cartId);
            
            cartService.clearCart(cartId);
            return ResponseEntity.ok()
                .body("Cart cleared successfully");
                
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body("Invalid request: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Error clearing cart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to clear cart: " + e.getMessage());
        }
    }
    
    /**
     * Get cart by ID
     */
    @GetMapping("/{cartId}")
    public ResponseEntity<?> getCartById(@PathVariable Long cartId) {
        try {
            if (cartId == null) {
                return ResponseEntity.badRequest()
                    .body("Cart ID is required");
            }
            
            Cart cart = cartService.getCart(cartId);
            return ResponseEntity.ok(cart);
            
        } catch (RuntimeException e) {
            System.err.println("Error getting cart: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Cart not found: " + e.getMessage());
        }
    }
}