package com.beytullahpaytar.ecommerce.controller;

import com.beytullahpaytar.ecommerce.dto.CartItemDto;
import com.beytullahpaytar.ecommerce.models.Cart;
import com.beytullahpaytar.ecommerce.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<Cart> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @PostMapping
    public ResponseEntity<String> addItemToCart(@RequestBody CartItemDto cartItemDto) {
        cartService.addItemToCart(cartItemDto);
        return ResponseEntity.ok("Item added to cart");
    }

    @DeleteMapping
    public ResponseEntity<String> clearCart() {
        cartService.clearCart();
        return ResponseEntity.ok("Cart cleared");
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<String> removeItemFromCart(@PathVariable Long cartItemId) {
        cartService.removeItemFromCart(cartItemId);
        return ResponseEntity.ok("Item removed from cart");
    }


}
