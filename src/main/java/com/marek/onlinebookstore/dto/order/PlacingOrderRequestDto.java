package com.marek.onlinebookstore.dto.order;

import jakarta.validation.constraints.NotNull;

public record PlacingOrderRequestDto(
        @NotNull
        String shippingAddress
) {
}

