package com.marek.onlinebookstore.service.book;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.marek.onlinebookstore.dto.book.BookDto;
import com.marek.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.marek.onlinebookstore.dto.book.CreateBookRequestDto;
import com.marek.onlinebookstore.exception.EntityNotFoundException;
import com.marek.onlinebookstore.mapper.BookMapper;
import com.marek.onlinebookstore.model.Book;
import com.marek.onlinebookstore.model.Category;
import com.marek.onlinebookstore.repository.book.BookRepository;
import com.marek.onlinebookstore.repository.category.CategoryRepository;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    private static final Long VALID_BOOK_ID = 1L;
    private static final Long INVALID_BOOK_ID = -123L;
    private static final Long CATEGORY_ID = 1L;

    private static final String TITLE = "Title";
    private static final String AUTHOR = "author";
    private static final String ISBN = "1231231";
    private static final BigDecimal PRICE = BigDecimal.valueOf(123.12);
    private static final String DESCRIPTION = "ok book";
    private static final String COVER_IMAGE = "image";
    private static final Set<Long> CATEGORIES_ID = Set.of();
    private static final Set<Category> CATEGORIES = Set.of();

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookMapper bookMapping;

    @InjectMocks
    private BookServiceImpl bookService;

    @AfterEach
    public void cleanUpTestData() {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("Verify output for correct input in save method")
    public void save_correctInput_ReturnsBookDtoWithoutCategoryIds() {
        Set<Long> categoriesId = new HashSet<>();
        categoriesId.add(CATEGORY_ID);

        Category category = getCategory(CATEGORY_ID);
        Set<Category> categories = new HashSet<>();
        categories.add(category);

        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                TITLE, AUTHOR, ISBN, PRICE, DESCRIPTION, COVER_IMAGE, categoriesId
        );
        Book book = getBook();
        book.setCategories(categories);

        BookDtoWithoutCategoryIds expected = getBookDtoWithoutCategoryIdsFromBook(book);

        when(bookMapping.toEntity(requestDto)).thenReturn(book);
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapping.toDtoWithoutCategoryIds(book)).thenReturn(expected);

        BookDtoWithoutCategoryIds actual = bookService.save(requestDto);
        Assertions.assertEquals(expected, actual);

        verify(bookMapping, times(1)).toEntity(requestDto);
        verify(bookRepository, times(1)).save(book);
        verify(categoryRepository, times(1)).findById(CATEGORY_ID);
        verify(bookMapping, times(1)).toDtoWithoutCategoryIds(book);
        verifyNoMoreInteractions(bookMapping, bookRepository, categoryRepository);
    }

    @Test
    @DisplayName("Verify findAll verify pageable")
    public void findAll_VerifyPageable_ReturnsList() {
        Book book = getBook();
        BookDto bookDto = getBookDtoFromBook(book);

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);

        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapping.toDto(book)).thenReturn(bookDto);

        List<BookDto> actual = bookService.findAll(pageable);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isEqualTo(bookDto);

        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapping, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapping);
    }

    @Test
    @DisplayName("Verify findById method with correct id")
    public void findById_withCorrectId_returnsBookDto() {
        Set<Long> categoriesId = new HashSet<>();
        categoriesId.add(CATEGORY_ID);

        Category category = getCategory(CATEGORY_ID);
        Set<Category> categories = new HashSet<>();
        categories.add(category);

        Book book = getBook();
        book.setCategories(categories);

        BookDto expected = getBookDtoFromBook(book);
        expected.setCategoriesId(categoriesId);

        when(bookRepository.findById(VALID_BOOK_ID)).thenReturn(Optional.of(book));
        when(bookMapping.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.findById(VALID_BOOK_ID);
        Assertions.assertEquals(expected, actual);

        verify(bookRepository, times(1)).findById(VALID_BOOK_ID);
        verify(bookMapping, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapping);
    }

    @Test
    @DisplayName("Verify findById method without correct id")
    public void findById_withoutCorrectId_throwException() {
        when(bookRepository.findById(INVALID_BOOK_ID)).thenReturn(Optional.empty());
        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(INVALID_BOOK_ID));

        Assertions.assertEquals("Can't find book with id: "
                + INVALID_BOOK_ID, exception.getMessage());

        verify(bookRepository, times(1)).findById(INVALID_BOOK_ID);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Verify deleteById with correct id should delete book from DB")
    public void deleteById_withCorrectId_deleteBookFromDB() {
        Book book = getBook();

        when(bookRepository.findById(VALID_BOOK_ID)).thenReturn(Optional.of(book));

        bookService.deleteById(VALID_BOOK_ID);

        verify(bookRepository, times(1)).findById(VALID_BOOK_ID);
        verify(bookRepository, times(1)).deleteById(VALID_BOOK_ID);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Verify update with correct id should return updated BookDto")
    public void update_withCorrectId_returnsBookDto() {
        CreateBookRequestDto createBookRequestDto = getCreateBookRequestDto();
        Book book = getBook();
        BookDto expected = getBookDtoFromBook(book);

        when(bookRepository.findById(VALID_BOOK_ID)).thenReturn(Optional.of(book));
        when(bookMapping.toEntity(createBookRequestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapping.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.update(VALID_BOOK_ID, createBookRequestDto);

        Assertions.assertEquals(expected, actual);

        verify(bookRepository, times(1)).findById(VALID_BOOK_ID);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapping, times(1)).toEntity(createBookRequestDto);
        verify(bookMapping, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapping);
    }

    @Test
    @DisplayName("Verify update without correct id should throw EntityNotFoundException")
    public void update_withoutCorrectId_throwException() {
        CreateBookRequestDto createBookRequestDto = getCreateBookRequestDto();

        when(bookRepository.findById(INVALID_BOOK_ID)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookService.update(INVALID_BOOK_ID, createBookRequestDto));

        Assertions.assertEquals("Can't find book with id: "
                + INVALID_BOOK_ID, exception.getMessage());

        verify(bookRepository, times(1)).findById(INVALID_BOOK_ID);
        verifyNoMoreInteractions(bookRepository);
    }

    private static @NotNull Book getBook() {
        Book book = new Book();
        book.setId(VALID_BOOK_ID);
        book.setTitle(TITLE);
        book.setAuthor(AUTHOR);
        book.setIsbn(ISBN);
        book.setPrice(PRICE);
        book.setDescription(DESCRIPTION);
        book.setCoverImage(COVER_IMAGE);
        book.setCategories(CATEGORIES);
        book.setDeleted(false);
        return book;
    }

    private static @NotNull Category getCategory(Long categoryId) {
        Category category = new Category();
        category.setId(categoryId);
        category.setName("category");
        category.setDescription(DESCRIPTION);
        category.setDeleted(false);
        return category;
    }

    private static @NotNull BookDto getBookDtoFromBook(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setCategoriesId(Set.of());
        bookDto.setTitle(book.getTitle());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setPrice(book.getPrice());
        bookDto.setId(book.getId());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        return bookDto;
    }

    private static
            @NotNull BookDtoWithoutCategoryIds getBookDtoWithoutCategoryIdsFromBook(Book book) {
        return new BookDtoWithoutCategoryIds(
                book.getId(), book.getTitle(), book.getAuthor(),
                book.getIsbn(), book.getPrice(), book.getDescription(),
                book.getCoverImage()
        );
    }

    private static @NotNull CreateBookRequestDto getCreateBookRequestDto() {
        return new CreateBookRequestDto(
                TITLE, AUTHOR, ISBN, PRICE, DESCRIPTION, COVER_IMAGE, CATEGORIES_ID
        );
    }
}
