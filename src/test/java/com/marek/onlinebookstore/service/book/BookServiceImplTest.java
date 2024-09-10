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
    public static final String TITLE = "Title";
    public static final String AUTHOR = "author";
    public static final String ISBN = "1231231";
    public static final BigDecimal PRICE = BigDecimal.valueOf(123.12);
    public static final String DESCRIPTION = "ok book";
    public static final String COVER_IMAGE = "image";
    public static final Set<Long> CATEGORIES_ID = Set.of();
    public static final Set<Category> CATEGORIES = Set.of();
    public static final Long CATEGORY_ID = 1L;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookMapper bookMapping;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Verify output for correct input in save method")
    public void save_correctInput_ReturnsBookDtoWithoutCategoryIds() {
        Set<Long> categoriesId = new HashSet<>();
        categoriesId.add(CATEGORY_ID);

        Category category = getCategory(CATEGORY_ID);

        Set<Category> categories = new HashSet<>();
        categories.add(category);

        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                TITLE, AUTHOR, ISBN,
                PRICE, DESCRIPTION,
                COVER_IMAGE, categoriesId
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

        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                TITLE, AUTHOR, ISBN,
                PRICE, DESCRIPTION,
                COVER_IMAGE, categoriesId
        );

        Book book = getBook();
        book.setCategories(categories);

        BookDto expected = getBookDtoFromBook(book);
        expected.setCategoriesId(categoriesId);

        Long bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapping.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.findById(bookId);
        Assertions.assertEquals(expected, actual);

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookMapping, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapping);
    }

    @Test
    @DisplayName("Verify findById method without correct id")
    public void findById_withoutCorrectId_throwException() {
        Long bookId = -100L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(bookId));

        Assertions.assertEquals("Can't find book with id: " + bookId, exception.getMessage());

        verify(bookRepository, times(1)).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Verify deleteById with correct id should delete book from DB")
    public void deleteById_withCorrectId_deleteBookFromDB() {
        Book book = getBook();
        Long bookId = book.getId();

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        bookService.deleteById(bookId);

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(bookId));

        Assertions.assertEquals("Can't find book with id: " + bookId, exception.getMessage());
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Verify update with correct id should update & return book")
    public void update_withCorrectId_returnsBookDto() {
        CreateBookRequestDto createBookRequestDto = getCreateBookRequestDto();
        Book book = getBook();
        Long bookId = book.getId();

        BookDto expected = getBookDtoFromBook(book);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapping.toEntity(createBookRequestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapping.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.update(bookId, createBookRequestDto);
        Assertions.assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookMapping, times(1)).toEntity(createBookRequestDto);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapping, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapping);
    }

    @Test
    @DisplayName("Verify update with correct id should throw a EntityNotFoundException")
    public void update_withoutCorrectId_throwException() {
        CreateBookRequestDto createBookRequestDto = getCreateBookRequestDto();
        Book book = getBook();
        book.setId(-123L);
        Long bookId = book.getId();

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookService.update(bookId, createBookRequestDto));
        Assertions.assertEquals("Can't find book with id: " + bookId, exception.getMessage());

        verify(bookRepository, times(1)).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Verify findByCategoryId with incorrect id "
            + "should throw an EntityNotFoundException")
    public void findByCategoryId_withIncorrectCategoryId_throwException() {
        Set<Long> categoriesId = new HashSet<>();
        categoriesId.add(CATEGORY_ID);

        Category category = getCategory(CATEGORY_ID);

        Set<Category> categories = new HashSet<>();
        categories.add(category);

        Book book = getBook();
        book.setCategories(categories);
        Long bookId = book.getId();

        Pageable pageable = PageRequest.of(0, 10);

        List<Book> bookList = List.of(book);
        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIdsFromBook =
                getBookDtoWithoutCategoryIdsFromBook(book);

        when(bookRepository.findAllByCategoryId(CATEGORY_ID, pageable)).thenReturn(new
                PageImpl<>(bookList));
        when(bookMapping.toDtoWithoutCategoryIds(book)).thenReturn(
                bookDtoWithoutCategoryIdsFromBook
        );
        List<BookDtoWithoutCategoryIds> byCategoryId = bookService
                .findByCategoryId(CATEGORY_ID, pageable);
        Assertions.assertEquals(bookDtoWithoutCategoryIdsFromBook, byCategoryId.get(0));

        verify(bookRepository, times(1))
                .findAllByCategoryId(CATEGORY_ID, pageable);
        verify(bookMapping, times(1)).toDtoWithoutCategoryIds(book);
        verifyNoMoreInteractions(bookRepository, bookMapping);
    }

    @Test
    @DisplayName("Verify findByCategoryId with correct id should return list of books")
    public void findByCategoryId_withCorrectCategoryId_returnsListOfBooks() {
        Long categoryId = 123123123123198669L;
        Pageable pageable = PageRequest.of(0, 10);

        List<Book> bookList = List.of();

        when(bookRepository.findAllByCategoryId(categoryId, pageable))
                .thenReturn(new PageImpl<>(bookList));
        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookService.findByCategoryId(categoryId, pageable));
        Assertions.assertEquals("Can't find books with category id: " + categoryId,
                exception.getMessage());
        verify(bookRepository, times(1)).findAllByCategoryId(categoryId, pageable);
        verifyNoMoreInteractions(bookRepository);
    }

    private static @NotNull Book getBook() {
        Book book = new Book();
        book.setId(1L);
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
