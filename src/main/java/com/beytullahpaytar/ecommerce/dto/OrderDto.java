package com.beytullahpaytar.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderDto(
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "Surname is required")
        String surname,
        @NotBlank(message = "Email is required")
        String email,
        @NotBlank(message = "Phone number is required")
        String phoneNumber,
        @NotBlank(message = "Address is required")
        String address
) {
}
