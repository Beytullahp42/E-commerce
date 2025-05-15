package com.beytullahpaytar.ecommerce.controller;

import com.beytullahpaytar.ecommerce.dto.OrderDto;
import com.beytullahpaytar.ecommerce.dto.UpdateOrderStatusDto;
import com.beytullahpaytar.ecommerce.models.Order;
import com.beytullahpaytar.ecommerce.services.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    void createOrder_shouldReturnSuccessMessage() throws Exception {
        // Arrange
        OrderDto dto = new OrderDto("John", "Doe", "john@example.com", "123456789", "123 Main St");
        String requestBody = objectMapper.writeValueAsString(dto);

        // Act & Assert
        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Order created successfully"));

        verify(orderService, times(1)).createOrder(any(OrderDto.class));
    }

    @Test
    void createOrder_shouldReturnBadRequestForInvalidInput() throws Exception {
        // Arrange - missing required fields
        OrderDto invalidDto = new OrderDto("", "", "", "", "");
        String requestBody = objectMapper.writeValueAsString(invalidDto);

        // Act & Assert
        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrder(any());
    }

    @Test
    void getAllOrders_shouldReturnListOfOrders() throws Exception {
        // Arrange
        Order order1 = new Order();
        order1.setId(1L);
        order1.setName("John");

        Order order2 = new Order();
        order2.setId(2L);
        order2.setName("Jane");

        List<Order> orders = Arrays.asList(order1, order2);
        when(orderService.getAllOrders()).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/api/order"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void getOrderById_shouldReturnOrderWhenExists() throws Exception {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setName("John Doe");
        when(orderService.getOrderById(1L)).thenReturn(order);

        // Act & Assert
        mockMvc.perform(get("/api/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    void getOrderById_shouldReturnNotFoundWhenNotExists() throws Exception {
        // Arrange
        when(orderService.getOrderById(999L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/order/999"))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).getOrderById(999L);
    }

    @Test
    void updateOrderStatus_shouldUpdateSuccessfully() throws Exception {
        // Arrange
        UpdateOrderStatusDto dto = new UpdateOrderStatusDto("SHIPPED");
        String requestBody = objectMapper.writeValueAsString(dto);

        // Act & Assert
        mockMvc.perform(put("/api/order/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Order status updated successfully"));

        verify(orderService, times(1)).updateOrderStatus(eq(1L), eq("SHIPPED"));
    }

    @Test
    void updateOrderStatus_shouldReturnBadRequestForInvalidInput() throws Exception {
        // Arrange - empty status
        String invalidRequestBody = "{\"orderStatus\":\"\"}";

        // Act & Assert
        mockMvc.perform(put("/api/order/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).updateOrderStatus(anyLong(), any());
    }
}