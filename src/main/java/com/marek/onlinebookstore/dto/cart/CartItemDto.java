package com.marek.onlinebookstore.dto.cart;

public record CartItemDto(
        Long id,
        Long bookId,
        String bookTitle,
        Long quantity
) {
}

