package com.marek.onlinebookstore.service.book;

import com.marek.onlinebookstore.dto.book.BookDto;
import com.marek.onlinebookstore.dto.book.BookSearchParametersDto;
import com.marek.onlinebookstore.dto.book.CreateBookRequestDto;
import com.marek.onlinebookstore.exception.EntityNotFoundException;
import com.marek.onlinebookstore.mapper.BooksMapper;
import com.marek.onlinebookstore.model.Book;
import com.marek.onlinebookstore.repository.book.BookRepository;
import com.marek.onlinebookstore.repository.book.BookSpecificationBuilder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BooksMapper bookMapping;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto createBookRequestDto) {
        Book book = bookMapping.toModel(createBookRequestDto);
        return bookMapping.toBookDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .stream()
                .map(bookMapping::toBookDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        return bookMapping.toBookDto(
                bookRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Can't find book with id: " + id))
        );
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
    public BookDto update(Long id, CreateBookRequestDto bookRequestDto) {
        if (!bookRepository.findById(id).isPresent()) {
            throw new EntityNotFoundException("Can't find book with id: " + id);
        }
        Book book = bookMapping.toModel(bookRequestDto);
        book.setId(id);
        return bookMapping.toBookDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> searchBooks(BookSearchParametersDto searchParameters, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParameters);
        return bookRepository.findAll(bookSpecification, pageable)
                .stream()
                .map(bookMapping::toBookDto)
                .toList();
    }
}

