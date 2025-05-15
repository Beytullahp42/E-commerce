package com.beytullahpaytar.ecommerce.controller;

import com.beytullahpaytar.ecommerce.dto.ItemDto;
import com.beytullahpaytar.ecommerce.models.Item;
import com.beytullahpaytar.ecommerce.services.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    void getItem_shouldReturnItemWhenExists() throws Exception {
        // Arrange
        Item mockItem = new Item(1L, "Test Item", "Description", 99.99, "image.jpg");
        when(itemService.getItem(1L)).thenReturn(mockItem);

        // Act & Assert
        mockMvc.perform(get("/api/admin/items/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.name").value("Test Item")).andExpect(jsonPath("$.price").value(99.99));

        verify(itemService, times(1)).getItem(1L);
    }

    @Test
    void getItem_shouldReturnNotFoundWhenItemDoesNotExist() throws Exception {
        // Arrange
        when(itemService.getItem(anyLong())).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/admin/items/999")).andExpect(status().isOk()).andExpect(content().string(""));

        verify(itemService, times(1)).getItem(999L);
    }

    @Test
    void getAllItems_shouldReturnListOfItems() throws Exception {
        // Arrange
        List<Item> items = Arrays.asList(new Item(1L, "Item 1", "Desc 1", 10.0, "img1.jpg"), new Item(2L, "Item 2", "Desc 2", 20.0, "img2.jpg"));
        when(itemService.getAllItems()).thenReturn(items);

        // Act & Assert
        mockMvc.perform(get("/api/admin/items")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2)).andExpect(jsonPath("$[0].name").value("Item 1")).andExpect(jsonPath("$[1].name").value("Item 2"));

        verify(itemService, times(1)).getAllItems();
    }

    @Test
    void addItem_shouldCreateNewItem() throws Exception {
        // Arrange
        ItemDto dto = new ItemDto("New Item", "New Description", 50.0, "tempFileimage.jpg");
        String requestBody = objectMapper.writeValueAsString(dto);

        when(itemService.addItem(any(ItemDto.class))).thenReturn(ResponseEntity.ok("Item added successfully"));

        // Act & Assert
        mockMvc.perform(post("/api/admin/items").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isOk()).andExpect(content().string("Item added successfully"));

        verify(itemService, times(1)).addItem(any(ItemDto.class));
    }

    @Test
    void addItem_shouldReturnBadRequestWhenInvalidInput() throws Exception {
        // Arrange - missing required fields
        ItemDto invalidDto = new ItemDto("", "", -10.0, "");
        String requestBody = objectMapper.writeValueAsString(invalidDto);

        // Act & Assert
        mockMvc.perform(post("/api/admin/items").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isBadRequest());

        verify(itemService, never()).addItem(any());
    }

    @Test
    void updateItem_shouldUpdateExistingItem() throws Exception {
        // Arrange
        ItemDto dto = new ItemDto("Updated Item", "Updated Desc", 60.0, "tempFileupdated.jpg");
        String requestBody = objectMapper.writeValueAsString(dto);

        when(itemService.updateItem(eq(1L), any(ItemDto.class))).thenReturn(ResponseEntity.ok("Item updated successfully"));

        // Act & Assert
        mockMvc.perform(put("/api/admin/items/1").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isOk()).andExpect(content().string("Item updated successfully"));

        verify(itemService, times(1)).updateItem(eq(1L), any(ItemDto.class));
    }

    @Test
    void updateItem_shouldReturnNotFoundForNonExistingItem() throws Exception {
        // Arrange
        ItemDto dto = new ItemDto("Item", "Desc", 10.0, "tempFileimage.jpg");
        String requestBody = objectMapper.writeValueAsString(dto);

        when(itemService.updateItem(eq(999L), any(ItemDto.class))).thenReturn(ResponseEntity.status(404).body("Item not found"));

        // Act & Assert
        mockMvc.perform(put("/api/admin/items/999").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isNotFound()).andExpect(content().string("Item not found"));

        verify(itemService, times(1)).updateItem(eq(999L), any(ItemDto.class));
    }

    @Test
    void updateItem_shouldReturnBadRequestWhenInvalidInput() throws Exception {
        // Arrange - invalid data
        ItemDto invalidDto = new ItemDto("", "", -5.0, "");
        String requestBody = objectMapper.writeValueAsString(invalidDto);

        // Act & Assert
        mockMvc.perform(put("/api/admin/items/1").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isBadRequest());

        verify(itemService, never()).updateItem(anyLong(), any());
    }
}