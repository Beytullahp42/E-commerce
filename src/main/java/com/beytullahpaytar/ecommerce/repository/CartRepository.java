package com.beytullahpaytar.ecommerce.repository;

import com.beytullahpaytar.ecommerce.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findFirstByIsCompletedFalse();
}
