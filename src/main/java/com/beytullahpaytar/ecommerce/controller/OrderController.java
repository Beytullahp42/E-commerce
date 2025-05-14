package com.beytullahpaytar.ecommerce.controller;

import com.beytullahpaytar.ecommerce.dto.OrderDto;
import com.beytullahpaytar.ecommerce.dto.UpdateOrderStatusDto;
import com.beytullahpaytar.ecommerce.models.Order;
import com.beytullahpaytar.ecommerce.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public String createOrder(@Valid @RequestBody OrderDto orderDto) {
        orderService.createOrder(orderDto);
        return "Order created successfully";
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateOrderStatus(@PathVariable Long id, @RequestBody UpdateOrderStatusDto dto) {
        orderService.updateOrderStatus(id, dto.orderStatus());
        return ResponseEntity.ok("Order status updated successfully");
    }
}
