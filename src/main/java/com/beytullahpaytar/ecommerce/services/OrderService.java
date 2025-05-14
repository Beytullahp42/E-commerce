package com.beytullahpaytar.ecommerce.services;

import com.beytullahpaytar.ecommerce.dto.OrderDto;
import com.beytullahpaytar.ecommerce.models.Order;
import com.beytullahpaytar.ecommerce.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
    }

    public void createOrder(OrderDto orderDto) {
        // Create a new order
        Order order = new Order();
        order.setName(orderDto.name());
        order.setSurname(orderDto.surname());
        order.setAddress(orderDto.address());
        order.setEmail(orderDto.email());
        order.setPhoneNumber(orderDto.phoneNumber());

        order.setCart(cartService.getCart());
        order.setTotalPrice(cartService.getTotalPrice());
        order.setOrderStatus("PENDING");

        System.out.println("Order Status: " + order.getOrderStatus());

        // Save the order to the database
        orderRepository.save(order);
        cartService.completeCart();
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public void updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setOrderStatus(status);
            orderRepository.save(order);
        }
    }
}
