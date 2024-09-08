package com.marek.onlinebookstore.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record OrderDto(
        @Positive
        Long id,
        @Positive
        Long userId,
        Set<OrderItemDto> orderItems,
        LocalDateTime orderDate,
        @PositiveOrZero
        BigDecimal total,
        @NotNull
        String status) {
}
