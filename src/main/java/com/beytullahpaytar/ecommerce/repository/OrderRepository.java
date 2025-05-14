package com.beytullahpaytar.ecommerce.repository;

import com.beytullahpaytar.ecommerce.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
