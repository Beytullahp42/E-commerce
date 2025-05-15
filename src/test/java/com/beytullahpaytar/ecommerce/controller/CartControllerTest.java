package com.beytullahpaytar.ecommerce.controller;

import com.beytullahpaytar.ecommerce.dto.CartItemDto;
import com.beytullahpaytar.ecommerce.models.Cart;
import com.beytullahpaytar.ecommerce.models.CartItem;
import com.beytullahpaytar.ecommerce.models.Item;
import com.beytullahpaytar.ecommerce.services.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CartControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
    }

    @Test
    void getCart_shouldReturnCurrentCart() throws Exception {
        // Arrange
        Cart cart = new Cart();
        cart.setId(1L);
        List<CartItem> items = new ArrayList<>();
        Item item = new Item(10L, "Test Item", "Description", 99.99, "image.jpg");
        items.add(new CartItem(1L, item, 2, cart));
        cart.setCartItems(items);

        when(cartService.getCart()).thenReturn(cart);

        // Act & Assert
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cartItems.length()").value(1))
                .andExpect(jsonPath("$.cartItems[0].item.name").value("Test Item"));

        verify(cartService, times(1)).getCart();
    }

    @Test
    void addItemToCart_shouldAddItemSuccessfully() throws Exception {
        // Arrange
        CartItemDto dto = new CartItemDto(10L, 2);
        String requestBody = objectMapper.writeValueAsString(dto);

        // Act & Assert
        mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Item added to cart"));

        verify(cartService, times(1)).addItemToCart(any(CartItemDto.class));
    }

    @Test
    void addItemToCart_shouldReturnBadRequestForInvalidInput() throws Exception {
        // Arrange - invalid quantity (0 or negative)
        CartItemDto invalidDto = new CartItemDto(10L, 0);
        String requestBody = objectMapper.writeValueAsString(invalidDto);

        // Act & Assert
        mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(cartService, never()).addItemToCart(any());
    }

    @Test
    void clearCart_shouldRemoveAllItems() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cart cleared"));

        verify(cartService, times(1)).clearCart();
    }

    @Test
    void removeItemFromCart_shouldRemoveSpecificItem() throws Exception {
        // Arrange
        Long cartItemId = 5L;

        // Act & Assert
        mockMvc.perform(delete("/api/cart/{cartItemId}", cartItemId))
                .andExpect(status().isOk())
                .andExpect(content().string("Item removed from cart"));

        verify(cartService, times(1)).removeItemFromCart(cartItemId);
    }

    @Test
    void removeItemFromCart_shouldReturnBadRequestForInvalidId() throws Exception {
        // Act & Assert - invalid ID format
        mockMvc.perform(delete("/api/cart/invalid"))
                .andExpect(status().isBadRequest());

        verify(cartService, never()).removeItemFromCart(anyLong());
    }
}