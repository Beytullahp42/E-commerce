package com.beytullahpaytar.ecommerce.services;

import com.beytullahpaytar.ecommerce.dto.ItemDto;
import com.beytullahpaytar.ecommerce.models.Item;
import com.beytullahpaytar.ecommerce.repository.ItemRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @TempDir
    Path tempUploadDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        itemService.setPath(tempUploadDir);
    }

    @Test
    void testAddItem_shouldSaveItemWithCorrectData() throws IOException {
        String originalFile = "tempFiletestImage.jpg";
        String finalFile = "testImage.jpg";
        Path tempFile = tempUploadDir.resolve(originalFile);
        Files.createFile(tempFile);

        ItemDto dto = new ItemDto("Test Item", "Nice item", 99.99, originalFile);

        // When
        ResponseEntity<String> response = itemService.addItem(dto);

        // Then
        assertEquals("Item added successfully", response.getBody());
        verify(itemRepository).save(argThat(item ->
                item.getName().equals("Test Item") &&
                        item.getImageUrl().equals(finalFile)
        ));

        assertTrue(Files.exists(tempUploadDir.resolve(finalFile)));
        assertFalse(Files.exists(tempFile));
    }

    @Test
    void testUpdateItem_shouldReplaceImageIfDifferent() throws IOException {
        // Given
        Item existingItem = new Item(1L, "Old", "Old", 10.0, "old.jpg");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        Path oldImage = tempUploadDir.resolve("old.jpg");
        Files.createFile(oldImage);

        Path newTempImage = tempUploadDir.resolve("tempFilenew.jpg");
        Files.createFile(newTempImage);

        ItemDto updatedDto = new ItemDto("Updated", "Updated", 20.0, "tempFilenew.jpg");

        // When
        ResponseEntity<String> response = itemService.updateItem(1L, updatedDto);

        // Then
        assertEquals("Item updated successfully", response.getBody());
        verify(itemRepository).save(argThat(item ->
                item.getName().equals("Updated") &&
                        item.getImageUrl().equals("new.jpg")
        ));
        assertTrue(Files.exists(tempUploadDir.resolve("new.jpg")));
        assertFalse(Files.exists(newTempImage));
        assertFalse(Files.exists(oldImage));
    }

    @Test
    void testUpdateItem_shouldReturnNotFoundIfItemDoesNotExist() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        ItemDto dto = new ItemDto("A", "B", 1.0, "tempFile_doesntmatter.jpg");

        ResponseEntity<String> response = itemService.updateItem(999L, dto);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Item not found", response.getBody());
    }

    @Test
    void testGetItem_shouldReturnCorrectItem() {
        Item item = new Item(1L, "Item", "Desc", 10.0, "img.jpg");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Item result = itemService.getItem(1L);

        assertNotNull(result);
        assertEquals("Item", result.getName());
    }

    @Test
    void testGetAllItems_shouldReturnList() {
        List<Item> mockList = List.of(
                new Item(1L, "Item1", "Desc1", 1.0, "img1.jpg"),
                new Item(2L, "Item2", "Desc2", 2.0, "img2.jpg")
        );
        when(itemRepository.findAll()).thenReturn(mockList);

        List<Item> itemList = itemService.getAllItems();

        assertEquals(2, itemList.size());
        assertEquals("Item1", itemList.getFirst().getName());
    }

    @Test
    void testHandleImageDelete_shouldDeleteFile() throws IOException {
        Path fileToDelete = tempUploadDir.resolve("delete-me.jpg");
        Files.createFile(fileToDelete);

        itemService.handleImageDelete("delete-me.jpg");

        assertFalse(Files.exists(fileToDelete));
    }

    @Test
    void testHandleImageUpload_shouldRenameFile() throws IOException {
        String inputFile = "tempFilesample.jpg";
        Path originalFile = tempUploadDir.resolve(inputFile);
        Files.createFile(originalFile);

        String result = itemService.handleImageUpload(inputFile);

        assertEquals("sample.jpg", result);
        assertTrue(Files.exists(tempUploadDir.resolve("sample.jpg")));
        assertFalse(Files.exists(originalFile));
    }
}
