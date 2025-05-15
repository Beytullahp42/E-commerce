package com.beytullahpaytar.ecommerce.services;

import com.beytullahpaytar.ecommerce.dto.OrderDto;
import com.beytullahpaytar.ecommerce.models.Cart;
import com.beytullahpaytar.ecommerce.models.Order;
import com.beytullahpaytar.ecommerce.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_shouldSaveOrderAndCompleteCart() {
        // Arrange
        Cart mockCart = new Cart();
        OrderDto dto = new OrderDto("John", "Doe", "john@example.com", "123456", "Some Address");
        when(cartService.getCart()).thenReturn(mockCart);
        when(cartService.getTotalPrice()).thenReturn(150.0);

        // Act
        orderService.createOrder(dto);

        // Assert
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();

        assertEquals("John", savedOrder.getName());
        assertEquals("PENDING", savedOrder.getOrderStatus());
        assertEquals(mockCart, savedOrder.getCart());
        assertEquals(150.0, savedOrder.getTotalPrice());

        verify(cartService).completeCart();
    }

    @Test
    void getAllOrders_shouldReturnAllOrders() {
        // Arrange
        Order o1 = new Order(); o1.setId(1L);
        Order o2 = new Order(); o2.setId(2L);
        when(orderRepository.findAll()).thenReturn(Arrays.asList(o1, o2));

        // Act
        List<Order> result = orderService.getAllOrders();

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void getOrderById_shouldReturnOrderIfExists() {
        Order order = new Order();
        order.setId(42L);
        when(orderRepository.findById(42L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(42L);

        assertNotNull(result);
        assertEquals(42L, result.getId());
    }

    @Test
    void getOrderById_shouldReturnNullIfNotExists() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        Order result = orderService.getOrderById(99L);

        assertNull(result);
    }

    @Test
    void updateOrderStatus_shouldUpdateIfExists() {
        Order order = new Order();
        order.setId(5L);
        order.setOrderStatus("PENDING");
        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));

        orderService.updateOrderStatus(5L, "SHIPPED");

        assertEquals("SHIPPED", order.getOrderStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_shouldDoNothingIfNotExists() {
        when(orderRepository.findById(100L)).thenReturn(Optional.empty());

        orderService.updateOrderStatus(100L, "CANCELLED");

        verify(orderRepository, never()).save(any());
    }
}
