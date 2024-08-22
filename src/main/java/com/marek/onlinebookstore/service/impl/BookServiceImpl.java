package com.marek.onlinebookstore.service.impl;

import com.marek.onlinebookstore.dto.BookDto;
import com.marek.onlinebookstore.dto.CreateBookRequestDto;
import com.marek.onlinebookstore.exception.EntityNotFoundException;
import com.marek.onlinebookstore.mapper.BooksMapper;
import com.marek.onlinebookstore.model.Book;
import com.marek.onlinebookstore.repository.BookRepository;
import com.marek.onlinebookstore.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BooksMapper bookMapping;

    @Override
    public BookDto save(CreateBookRequestDto createBookRequestDto) {
        Book book = bookMapping.toModel(createBookRequestDto);
        return bookMapping.toBookDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapping::toBookDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        return bookMapping.toBookDto(
                bookRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Can't find book with id: " + id)));
    }

    @Override
    public void deleteById(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Can't delete book with id: "
                    + id + " because it does not exist.");
        }
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto update(Long id, CreateBookRequestDto book) {
        Book modelBook = bookMapping.toModel(book);
        modelBook.setId(id);
        return bookMapping.toBookDto(bookRepository.save(modelBook));
    }
}
