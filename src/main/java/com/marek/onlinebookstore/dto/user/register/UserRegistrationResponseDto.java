package com.marek.onlinebookstore.dto.user.register;

public record UserRegistrationResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String shippingAddress
) {}


