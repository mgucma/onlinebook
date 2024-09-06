package com.marek.onlinebookstore.service.book;

import com.marek.onlinebookstore.dto.book.BookDto;
import com.marek.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.marek.onlinebookstore.dto.book.BookSearchParametersDto;
import com.marek.onlinebookstore.dto.book.CreateBookRequestDto;
import com.marek.onlinebookstore.exception.EntityNotFoundException;
import com.marek.onlinebookstore.mapper.BookMapper;
import com.marek.onlinebookstore.model.Book;
import com.marek.onlinebookstore.repository.book.BookRepository;
import com.marek.onlinebookstore.repository.book.BookSpecificationBuilder;
import com.marek.onlinebookstore.repository.category.CategoryRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapping;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDtoWithoutCategoryIds save(CreateBookRequestDto createBookRequestDto) {
        Book book = bookMapping.toEntity(createBookRequestDto);
        book.setCategories(createBookRequestDto.categoriesId().stream()
                .map(categoryRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet())
        );
        return bookMapping.toDtoWithoutCategoryIds(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapping::toDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        return bookMapping.toDto(
                bookRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Can't find book with id: " + id)));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto update(Long id, CreateBookRequestDto createBookRequestDto) {
        if (!bookRepository.findById(id).isPresent()) {
            throw new EntityNotFoundException("Can't find book with id: " + id);
        }
        Book book = bookMapping.toEntity(createBookRequestDto);
        book.setId(id);
        return bookMapping.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> searchBooks(BookSearchParametersDto searchParameters, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParameters);
        return bookRepository.findAll(bookSpecification, pageable)
                .stream().map(bookMapping::toDto).toList(); // Zwracamy List<BookDto>
    }

    @Override
    public List<BookDtoWithoutCategoryIds> findByCategoryId(Long id, Pageable pageable) {
        List<Book> allByCategoriesId = bookRepository.findAllByCategoriesId(id, pageable);
        if (allByCategoriesId.isEmpty()) {
            throw new EntityNotFoundException("Can't find books with category id: " + id);
        }
        return allByCategoriesId.stream()
                .map(bookMapping::toDtoWithoutCategoryIds)
                .toList();
    }
}
