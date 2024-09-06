package com.marek.onlinebookstore.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Set;

public record CreateBookRequestDto(
        @NotBlank
        String title,
        @NotBlank
        String author,
        @NotBlank
        String isbn,
        @NotNull
        @PositiveOrZero
        BigDecimal price,
        @NotBlank
        String description,
        @NotBlank
        String coverImage,
        Set<Long> categoriesId
) {
}
