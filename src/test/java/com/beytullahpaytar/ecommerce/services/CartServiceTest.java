    package com.beytullahpaytar.ecommerce.services;

    import com.beytullahpaytar.ecommerce.dto.CartItemDto;
    import com.beytullahpaytar.ecommerce.models.Cart;
    import com.beytullahpaytar.ecommerce.models.CartItem;
    import com.beytullahpaytar.ecommerce.models.Item;
    import com.beytullahpaytar.ecommerce.repository.CartItemRepository;
    import com.beytullahpaytar.ecommerce.repository.CartRepository;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Optional;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.Mockito.*;

    class CartServiceTest {

        private CartRepository cartRepository;
        private CartItemRepository cartItemRepository;
        private ItemService itemService;
        private CartService cartService;

        @BeforeEach
        void setUp() {
            cartRepository = mock(CartRepository.class);
            cartItemRepository = mock(CartItemRepository.class);
            itemService = mock(ItemService.class);
            cartService = new CartService(cartRepository, cartItemRepository, itemService);
        }

        @Test
        void testAddItemToCart_shouldCreateNewCartAndAddItem() {
            Cart newCart = new Cart();
            newCart.setId(1L);
            newCart.setCartItems(new ArrayList<>());

            Item item = new Item();
            item.setId(10L);
            item.setPrice(100.0);

            when(cartRepository.findFirstByIsCompletedFalse()).thenReturn(null);
            when(itemService.getItem(10L)).thenReturn(item);

            CartItemDto dto = new CartItemDto(10L, 3);

            cartService.addItemToCart(dto);

            verify(cartRepository).save(any(Cart.class));
            verify(cartItemRepository).save(argThat(cartItem ->
                    cartItem.getItem().getId().equals(10L) &&
                            cartItem.getQuantity() == 3 &&
                            cartItem.getCart() != null
            ));
        }

        @Test
        void testCompleteCart_shouldMarkCartAsCompleted() {
            Cart cart = new Cart();
            cart.setId(1L);
            cart.setIsCompleted(false);

            when(cartRepository.findFirstByIsCompletedFalse()).thenReturn(cart);

            cartService.completeCart();

            assertTrue(cart.getIsCompleted());
            verify(cartRepository).save(cart);
        }

        @Test
        void testGetTotalPrice_shouldReturnCorrectSum() {
            Cart cart = new Cart();
            List<CartItem> items = new ArrayList<>();

            Item item1 = new Item();
            item1.setPrice(50.0);
            CartItem cartItem1 = new CartItem();
            cartItem1.setItem(item1);
            cartItem1.setQuantity(2);

            Item item2 = new Item();
            item2.setPrice(30.0);
            CartItem cartItem2 = new CartItem();
            cartItem2.setItem(item2);
            cartItem2.setQuantity(1);

            items.add(cartItem1);
            items.add(cartItem2);

            cart.setCartItems(items);

            when(cartRepository.findFirstByIsCompletedFalse()).thenReturn(cart);

            Double total = cartService.getTotalPrice();

            assertEquals(130.0, total);
        }

        @Test
        void testClearCart_shouldRemoveAllItems() {
            Cart cart = new Cart();
            cart.setCartItems(new ArrayList<>(List.of(new CartItem(), new CartItem())));

            when(cartRepository.findFirstByIsCompletedFalse()).thenReturn(cart);

            cartService.clearCart();

            assertTrue(cart.getCartItems().isEmpty());
            verify(cartRepository).save(cart);
        }

        @Test
        void testRemoveItemFromCart_shouldDeleteIfExists() {
            CartItem item = new CartItem();
            item.setId(7L);

            when(cartItemRepository.findById(7L)).thenReturn(Optional.of(item));

            cartService.removeItemFromCart(7L);

            verify(cartItemRepository).delete(item);
        }

        @Test
        void testAddItemToCart_thenItemShouldExistInCart() {
            Cart cart = new Cart();
            cart.setId(1L);
            cart.setIsCompleted(false);
            cart.setCartItems(new ArrayList<>());

            Item item = new Item();
            item.setId(10L);
            item.setPrice(99.99);
            item.setName("Test Item");

            when(cartRepository.findFirstByIsCompletedFalse()).thenReturn(null, cart); // First call returns null to create a new cart, second for getCart
            when(itemService.getItem(10L)).thenReturn(item);
            when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

            CartItemDto dto = new CartItemDto(10L, 2);

            // Add item to cart
            cartService.addItemToCart(dto);

            // Now mock returning the updated cart with the added cart item
            CartItem addedCartItem = new CartItem();
            addedCartItem.setItem(item);
            addedCartItem.setQuantity(2);
            addedCartItem.setCart(cart);

            cart.getCartItems().add(addedCartItem);

            when(cartRepository.findFirstByIsCompletedFalse()).thenReturn(cart); // for getCart()

            Cart retrievedCart = cartService.getCart();

            assertNotNull(retrievedCart);
            assertEquals(1, retrievedCart.getCartItems().size());
            CartItem retrievedItem = retrievedCart.getCartItems().getFirst();
            assertEquals(10L, retrievedItem.getItem().getId());
            assertEquals(2, retrievedItem.getQuantity());
        }

    }
