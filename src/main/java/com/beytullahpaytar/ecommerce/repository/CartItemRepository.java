package com.beytullahpaytar.ecommerce.repository;

import com.beytullahpaytar.ecommerce.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByItemIdAndCartId(Long itemId, Long cartId);
}
