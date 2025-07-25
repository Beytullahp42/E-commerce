package com.beytullahpaytar.ecommerce.services;

import com.beytullahpaytar.ecommerce.dto.ItemDto;
import com.beytullahpaytar.ecommerce.models.Item;
import com.beytullahpaytar.ecommerce.repository.ItemRepository;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    @Setter //for test purposes
    Path path = Paths.get("upload-dir");

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public ResponseEntity<String> addItem(ItemDto dto) {
        Item item = new Item();
        item.setName(dto.name());
        item.setDescription(dto.description());
        item.setPrice(dto.price());

        item.setImageUrl(handleImageUpload(dto.imageUrl()));

        itemRepository.save(item);

        return ResponseEntity.ok("Item added successfully");
    }


    public ResponseEntity<String> updateItem(Long id, ItemDto dto) {
        Item item = itemRepository.findById(id).orElse(null);
        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Item not found");
        }
        item.setName(dto.name());
        item.setDescription(dto.description());
        item.setPrice(dto.price());

        if(!Objects.equals(dto.imageUrl(), item.getImageUrl())) {
            handleImageDelete(item.getImageUrl());
            item.setImageUrl(handleImageUpload(dto.imageUrl()));
        }

        itemRepository.save(item);
        return ResponseEntity.ok("Item updated successfully");
    }

    public Item getItem(Long id) {
        return itemRepository.findById(id).orElse(null);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public String handleImageUpload(String filename) {

        String newFilename = filename.replaceFirst("tempFile", "");

        Path filePath = path.resolve(filename);
        Path newFilePath = path.resolve(newFilename);

        try {
            Files.copy(filePath, newFilePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to rename file: " + e.getMessage());
        }

        return newFilename;
    }

    public void handleImageDelete(String filename) {
        Path filePath = path.resolve(filename);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }
    }
}
