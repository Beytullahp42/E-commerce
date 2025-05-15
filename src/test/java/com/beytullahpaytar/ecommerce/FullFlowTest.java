package com.beytullahpaytar.ecommerce;

import com.beytullahpaytar.ecommerce.dto.*;
import com.beytullahpaytar.ecommerce.models.*;
import com.beytullahpaytar.ecommerce.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FullFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;


    private Path uploadPath;


    @BeforeEach
    void setUp() throws IOException {
        uploadPath = Paths.get("upload-dir");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up test files
        Files.walk(uploadPath)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        System.err.println("Failed to delete file: " + path);
                    }
                });
    }


    @Test
    public void testFullEcommerceFlow() throws Exception {

        // 0. Upload needed images

        Path tempImage1 = uploadPath.resolve("tempFiletest-image.jpg");
        Path tempImage2 = uploadPath.resolve("tempFileupdated-image.jpg");

        Files.createFile(tempImage1);
        Files.write(tempImage1, "test image content".getBytes());
        Files.createFile(tempImage2);
        Files.write(tempImage2, "updated image content".getBytes());

        // 1. Create an item

        ItemDto itemDto = new ItemDto(
                "Test Product",
                "This is a test product description",
                99.99,
                "tempFiletest-image.jpg"
        );

        mockMvc.perform(post("/api/admin/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(itemDto)
                        )
                )
                .andExpect(status().isOk())
                .andReturn();

        // Verify item was created
        Item createdItem = itemRepository.findItemsByName("Test Product");
        assertNotNull(createdItem);
        Long createdItemId = createdItem.getId();
        assertEquals(99.99, createdItem.getPrice());

        // 2. Update the item

        ItemDto updatedItemDto = new ItemDto(
                "Updated Product",
                "Updated description",
                129.99,
                "tempFileupdated-image.jpg"
        );

        mockMvc.perform(put("/api/admin/items/" + createdItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItemDto)))
                .andExpect(status().isOk());

        // Verify item was updated
        Item updatedItem = itemRepository.findById(createdItemId).orElse(null);
        assertNotNull(updatedItem);
        assertEquals("Updated Product", updatedItem.getName());
        assertEquals(129.99, updatedItem.getPrice());

        // 3. Add item to cart
        CartItemDto cartItemDto = new CartItemDto(createdItemId, 2);

        mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDto)))
                .andExpect(status().isOk());

        // Verify cart contains the item
        Cart activeCart = cartRepository.findFirstByIsCompletedFalse();
        assertNotNull(activeCart);
        assertEquals(1, activeCart.getCartItems().size());
        assertEquals(2, activeCart.getCartItems().getFirst().getQuantity());

        // 4. Create an order
        OrderDto orderDto = new OrderDto(
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                "123 Main St, City"
        );

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isOk());

        // Verify order was created
        List<Order> orders = orderRepository.findAll();
        assertEquals(1, orders.size());
        Order createdOrder = orders.getFirst();
        Long orderId = createdOrder.getId();

        assertEquals("John", createdOrder.getName());
        assertEquals("Doe", createdOrder.getSurname());
        assertEquals("PENDING", createdOrder.getOrderStatus());
        assertEquals(259.98, createdOrder.getTotalPrice()); // 129.99 * 2

        // Verify cart was completed
        Cart completedCart = cartRepository.findById(activeCart.getId()).orElse(null);
        assertNotNull(completedCart);
        assertTrue(completedCart.getIsCompleted());

        // 5. Update order status
        UpdateOrderStatusDto statusDto = new UpdateOrderStatusDto("SHIPPED");

        mockMvc.perform(put("/api/order/" + orderId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isOk());

        // Verify order status was updated
        Order updatedOrder = orderRepository.findById(orderId).orElse(null);
        assertNotNull(updatedOrder);
        assertEquals("SHIPPED", updatedOrder.getOrderStatus());
    }
}