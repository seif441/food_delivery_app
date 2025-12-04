package com.system.food_delivery_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.system.food_delivery_app.model.Cart;
import com.system.food_delivery_app.repository.ProductRepository;
import com.system.food_delivery_app.model.CartItem;
import com.system.food_delivery_app.model.Product;
import com.system.food_delivery_app.repository.CartRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;

    public Cart getCart(Long CartId) {
        return cartRepository.findById(CartId).orElseThrow(() -> new RuntimeException("Cart Not Found"));
    }

    @Transactional
    public Cart addItemToCart(Long CartId, Long ProductId, int quantity) {
        Cart cart = getCart(CartId);
        Product product = productRepository.findById(ProductId)
                .orElseThrow(() -> new RuntimeException("Product Not Found"));
        Optional<CartItem> exsistingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(ProductId))
                .findFirst();

        if (exsistingItem.isPresent()) {
            CartItem item = exsistingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.calculateSubTotal();
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setCart(cart);
            newItem.calculateSubTotal();
            cart.getItems().add(newItem);
        }
        cart.calculateTotal();
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItemFromCart(Long CartId, Long ProductId) {
        Cart cart = getCart(CartId);

        boolean removed = cart.getItems().removeIf(item -> item.getProduct().getId().equals(ProductId));

        if (removed) {
            cart.calculateTotal();
            return cartRepository.save(cart);
        }
        return cart;
    }

    public Cart UpdataItemQuantity(int quantity, Long ProductId, Long CartId) {
        Cart cart = getCart(CartId);
        Optional<CartItem> itemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(ProductId))
                .findFirst();

        if (itemOpt.isPresent()) {
            CartItem item = itemOpt.get();
            if (quantity <= 0) {
                return removeItemFromCart(CartId, ProductId);
            }
            item.setQuantity(quantity);
            item.calculateSubTotal();
            cart.calculateTotal();
            return cartRepository.save(cart);
        }
        throw new RuntimeException("item not found in cart");
    }

    @Transactional
    public void clearCart(Long CartId) {
        Cart cart = getCart(CartId);
        cart.getItems().clear();
        cart.calculateTotal();
        cartRepository.save(cart);
    }

}
