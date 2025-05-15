package com.beytullahpaytar.ecommerce.repository;

import com.beytullahpaytar.ecommerce.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Item findItemsByName(String name);
}

