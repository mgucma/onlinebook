package com.marek.onlinebookstore.service.book;

import com.marek.onlinebookstore.dto.book.BookDto;
import com.marek.onlinebookstore.dto.book.BookSearchParametersDto;
import com.marek.onlinebookstore.dto.book.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto createBookRequestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    void deleteById(Long id);

    BookDto update(Long id, CreateBookRequestDto createBookRequestDto);

    List<BookDto> searchBooks(BookSearchParametersDto searchParameters, Pageable pageable);
}
