package com.marek.onlinebookstore.service;

import com.marek.onlinebookstore.dto.BookDto;
import com.marek.onlinebookstore.dto.BookSearchParametersDto;
import com.marek.onlinebookstore.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto createBookRequestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);

    void deleteById(Long id);

    BookDto update(Long id, CreateBookRequestDto createBookRequestDto);

    List<BookDto> searchBooks(BookSearchParametersDto searchParameters);
}
