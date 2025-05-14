package com.beytullahpaytar.ecommerce.controller;

import com.beytullahpaytar.ecommerce.dto.ItemDto;
import com.beytullahpaytar.ecommerce.models.Item;
import com.beytullahpaytar.ecommerce.services.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItem(id));
    }

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return itemService.getAllItems();
    }

    @PostMapping
    public ResponseEntity<String> addItem(@Valid @RequestBody ItemDto dto) {
        return itemService.addItem(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateItem(@PathVariable Long id, @Valid @RequestBody ItemDto dto) {
        return itemService.updateItem(id, dto);
    }

}
