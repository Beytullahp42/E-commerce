package com.beytullahpaytar.ecommerce.dto;


import jakarta.validation.constraints.Positive;

public record CartItemDto(
        Long itemId,
        @Positive
        int quantity
) {
}


