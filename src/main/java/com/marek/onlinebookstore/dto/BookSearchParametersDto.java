package com.marek.onlinebookstore.dto;

public record BookSearchParametersDto(
        String[] title,
        String[] author,
        String[] isbn
) {
}
