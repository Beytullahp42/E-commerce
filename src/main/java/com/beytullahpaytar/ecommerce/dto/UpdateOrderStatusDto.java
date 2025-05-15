package com.beytullahpaytar.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateOrderStatusDto(
        @NotBlank
        String orderStatus
) {
}
