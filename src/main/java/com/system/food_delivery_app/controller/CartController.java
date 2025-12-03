package com.system.food_delivery_app.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.system.food_delivery_app.model.Cart;
import com.system.food_delivery_app.dto.CartItemRequest;
import com.system.food_delivery_app.service.CartService;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long cartId){
        Cart cart = cartService.getCart(cartId);
        return ResponseEntity.ok(cart);
    }
    @PostMapping("/{cartId}/items")
    public ResponseEntity<Cart> addItemToCart(@PathVariable Long cartId
                                            ,@RequestBody CartItemRequest request){
    Cart updatedCart = cartService.addItemToCart(cartId, request.getProductId(), request.getQuantity());
    return ResponseEntity.ok(updatedCart);
    }
    @PutMapping("/{cartId}/items")
    public ResponseEntity<Cart> UpdataItemQuantity(@PathVariable Long cartId
                                                ,@RequestBody CartItemRequest request){
    Cart updatedCart = cartService.UpdataItemQuantity(request.getQuantity(), request.getProductId(), cartId);
    return ResponseEntity.ok(updatedCart);
    }
    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<Cart> removeItemFromCart(@PathVariable Long cartId
                                                ,@RequestParam Long ProductId){
    Cart updatedCart = cartService.removeItemFromCart(cartId, ProductId);
    return ResponseEntity.ok(updatedCart);
    }
    
}
