package com.marek.onlinebookstore.dto.book;

public record BookSearchParametersDto(
        String[] title,
        String[] author,
        String[] isbn
) {
}
