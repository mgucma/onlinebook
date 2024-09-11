package com.marek.onlinebookstore.controller;

import com.marek.onlinebookstore.dto.book.BookDto;
import com.marek.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.marek.onlinebookstore.dto.book.BookSearchParametersDto;
import com.marek.onlinebookstore.dto.book.CreateBookRequestDto;
import com.marek.onlinebookstore.service.book.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book management", description = "Endpoints for managing books")
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Get all books", description = "get a list of all available books")
    public ResponseEntity<List<BookDto>> getAll(Pageable pageable) {
        List<BookDto> books = bookService.findAll(pageable);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Get a book by id", description = "get book with your id")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        try {
            BookDto book = bookService.findById(id);
            return ResponseEntity.ok(book);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create book", description = "create new book")
    public ResponseEntity<BookDtoWithoutCategoryIds> createBook(
            @RequestBody @Valid CreateBookRequestDto createBookRequestDto) {
        BookDtoWithoutCategoryIds createdBook = bookService.save(createBookRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update book", description = "update book with your id")
    public ResponseEntity<BookDto> updateBookById(
            @PathVariable Long id,
            @RequestBody @Valid CreateBookRequestDto createBookRequestDto) {
        try {
            BookDto updatedBook = bookService.update(id, createBookRequestDto);
            return ResponseEntity.ok(updatedBook);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Search Books", description = "search book with your parameters")
    public ResponseEntity<List<BookDto>> searchBooks(
            BookSearchParametersDto searchParameters, Pageable pageable) {
        List<BookDto> books = bookService.searchBooks(searchParameters, pageable);
        return ResponseEntity.ok(books);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete book", description = "delete book with your id")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            bookService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
