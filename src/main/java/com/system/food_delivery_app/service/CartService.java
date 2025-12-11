package com.system.food_delivery_app.service;

import org.springframework.stereotype.Service;
import com.system.food_delivery_app.model.Cart;
import com.system.food_delivery_app.model.CartItem;
import com.system.food_delivery_app.model.Customer;
import com.system.food_delivery_app.model.Product;
import com.system.food_delivery_app.repository.CartRepository;
import com.system.food_delivery_app.repository.CustomerRepository;
import com.system.food_delivery_app.repository.ProductRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class CartService {
    
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public CartService(CartRepository cartRepository, 
                       ProductRepository productRepository, 
                       CustomerRepository customerRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * FIXED: Get existing cart or Create a new one
     * Added comprehensive validation and error handling
     */
    @Transactional
    public Cart getOrCreateCart(Long customerId) {
        // FIXED: Validate input
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        
        // Try to find existing cart
        Cart cart = cartRepository.findByCustomerId(customerId);
        
        if (cart == null) {
            // FIXED: Verify customer exists before creating cart
            Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException(
                    "Customer not found with ID: " + customerId + 
                    ". Please ensure user is properly registered."
                ));
            
            // Create new cart
            cart = new Cart();
            cart.setCustomer(customer);
            cart.setItems(new ArrayList<>()); // Initialize empty list
            cart = cartRepository.save(cart);
            
            System.out.println("Created new cart with ID: " + cart.getId() + " for customer: " + customerId);
        }
        
        // Ensure totals are calculated
        cart.calculateTotal();
        return cart;
    }

    /**
     * Get existing cart by ID
     */
    public Cart getCart(Long cartId) {
        if (cartId == null) {
            throw new IllegalArgumentException("Cart ID cannot be null");
        }
        
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with ID: " + cartId));
        cart.calculateTotal();
        return cart;
    }

    /**
     * FIXED: Add item to cart with comprehensive validation
     */
    @Transactional
    public Cart addItemToCart(Long cartId, Long productId, int quantity) {
        // FIXED: Validate all inputs
        if (cartId == null) {
            throw new IllegalArgumentException("Cart ID cannot be null");
        }
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive, got: " + quantity);
        }
        
        // Get cart
        Cart cart = getCart(cartId);
        
        // FIXED: Validate product exists and is available
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
        
        if (!product.isAvailable()) {
            throw new RuntimeException("Product '" + product.getName() + "' is not available");
        }
        
        // Check if item already exists in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update existing item quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.calculateSubTotal();
            System.out.println("Updated existing item. New quantity: " + item.getQuantity());
        } else {
            // Add new item
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setCart(cart);
            newItem.calculateSubTotal();
            cart.getItems().add(newItem);
            System.out.println("Added new item: " + product.getName() + " x" + quantity);
        }
        
        cart.calculateTotal();
        Cart savedCart = cartRepository.save(cart);
        
        System.out.println("Cart updated. Total items: " + savedCart.getItems().size() + 
                         ", Total price: $" + savedCart.getTotalPrice());
        
        return savedCart;
    }

    /**
     * FIXED: Remove item with validation
     */
    @Transactional
    public Cart removeItemFromCart(Long cartId, Long productId) {
        if (cartId == null || productId == null) {
            throw new IllegalArgumentException("Cart ID and Product ID cannot be null");
        }
        
        Cart cart = getCart(cartId);
        boolean removed = cart.getItems().removeIf(
            item -> item.getProduct().getId().equals(productId)
        );

        if (!removed) {
            System.out.println("Warning: Product " + productId + " not found in cart");
        }
        
        cart.calculateTotal();
        return cartRepository.save(cart);
    }

    /**
     * FIXED: Update item quantity with validation
     */
    @Transactional
    public Cart updateItemQuantity(Long cartId, Long productId, int quantity) {
        if (cartId == null || productId == null) {
            throw new IllegalArgumentException("Cart ID and Product ID cannot be null");
        }
        
        // If quantity is 0 or negative, remove the item
        if (quantity <= 0) {
            return removeItemFromCart(cartId, productId);
        }
        
        Cart cart = getCart(cartId);
        Optional<CartItem> itemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (itemOpt.isPresent()) {
            CartItem item = itemOpt.get();
            item.setQuantity(quantity);
            item.calculateSubTotal();
            cart.calculateTotal();
            return cartRepository.save(cart);
        }
        
        throw new RuntimeException("Item not found in cart with product ID: " + productId);
    }

    /**
     * Clear all items from cart
     */
    @Transactional
    public void clearCart(Long cartId) {
        if (cartId == null) {
            throw new IllegalArgumentException("Cart ID cannot be null");
        }
        
        Cart cart = getCart(cartId);
        cart.getItems().clear();
        cart.calculateTotal();
        cartRepository.save(cart);
        
        System.out.println("Cleared cart ID: " + cartId);
    }
    
    /**
     * Delete cart completely
     */
    @Transactional
    public void deleteCart(Long cartId) {
        if (cartId == null) {
            throw new IllegalArgumentException("Cart ID cannot be null");
        }
        
        cartRepository.deleteById(cartId);
        System.out.println("Deleted cart ID: " + cartId);
    }
    
    /**
     * Get cart by customer ID (without creating if not exists)
     */
    public Optional<Cart> findCartByCustomerId(Long customerId) {
        if (customerId == null) {
            return Optional.empty();
        }
        
        Cart cart = cartRepository.findByCustomerId(customerId);
        if (cart != null) {
            cart.calculateTotal();
        }
        return Optional.ofNullable(cart);
    }
}