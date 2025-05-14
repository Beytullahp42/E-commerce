package com.beytullahpaytar.ecommerce.services;

import com.beytullahpaytar.ecommerce.dto.CartItemDto;
import com.beytullahpaytar.ecommerce.models.Cart;
import com.beytullahpaytar.ecommerce.models.CartItem;
import com.beytullahpaytar.ecommerce.models.Item;
import com.beytullahpaytar.ecommerce.repository.CartItemRepository;
import com.beytullahpaytar.ecommerce.repository.CartRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemService itemService;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ItemService itemService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.itemService = itemService;
    }

    public Cart getCart(){
        Cart cart = cartRepository.findFirstByIsCompletedFalse();
        if (cart == null) {
            cart = new Cart();
            cartRepository.save(cart);
        }
        return cart;
    }

    public void addItemToCart(CartItemDto cartItemDto) {
        Cart cart = getCart();

        // Check if the item already exists in the cart
        CartItem cartItem = cartItemRepository.findByItemIdAndCartId(cartItemDto.itemId(), cart.getId());
        if(cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + cartItemDto.quantity());
            cartItemRepository.save(cartItem);
            return;
        }

        cartItem = new CartItem();
        Item item = itemService.getItem(cartItemDto.itemId());
        cartItem.setItem(item);
        cartItem.setQuantity(cartItemDto.quantity());
        cartItem.setCart(cart);
        cartItemRepository.save(cartItem);
    }

    public void removeItemFromCart(Long cartItemId) {
        cartItemRepository.findById(cartItemId).ifPresent(cartItemRepository::delete);
    }

    public void clearCart() {
        Cart cart = getCart();
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    public void completeCart() {
        Cart cart = getCart();
        cart.setIsCompleted(true);
        cartRepository.save(cart);
    }

    public Double getTotalPrice() {
        Cart cart = getCart();
        double totalPrice = 0.0;
        for (CartItem cartItem : cart.getCartItems()) {
            totalPrice += cartItem.getItem().getPrice() * cartItem.getQuantity();
        }
        return totalPrice;
    }

}
