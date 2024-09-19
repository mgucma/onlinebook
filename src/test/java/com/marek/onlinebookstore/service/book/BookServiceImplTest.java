package com.marek.onlinebookstore.service.book;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Save book with valid input - returns BookDtoWithoutCategoryIds")
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

        when(bookMapper.toEntity(requestDto)).thenReturn(book);
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDtoWithoutCategoryIds(book)).thenReturn(expected);

        BookDtoWithoutCategoryIds actual = bookService.save(requestDto);

        Assertions.assertEquals(expected, actual);

        verify(bookMapper, times(1)).toEntity(requestDto);
        verify(bookRepository, times(1)).save(book);
        verify(categoryRepository, times(1)).findById(CATEGORY_ID);
        verify(bookMapper, times(1)).toDtoWithoutCategoryIds(book);
        verifyNoMoreInteractions(bookMapper, bookRepository, categoryRepository);
    }

    @Test
    @DisplayName("Find all books with pagination - verify pageable")
    public void findAll_VerifyPageable_ReturnsList() {
        Book book = getBook();
        BookDto bookDto = getBookDtoFromBook(book);

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);

        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> actual = bookService.findAll(pageable);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isEqualTo(bookDto);

        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find book by valid ID - returns BookDto")
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

        Long bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.findById(bookId);

        Assertions.assertEquals(expected, actual);

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find book by invalid ID - throws EntityNotFoundException")
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
    @DisplayName("Delete book by valid ID - book removed from DB")
    public void deleteById_withCorrectId_deleteBookFromDB() {
        Book book = getBook();
        Long bookId = book.getId();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        bookService.deleteById(bookId);

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Update book by valid ID - returns updated BookDto")
    public void update_withCorrectId_returnsBookDto() {
        CreateBookRequestDto createBookRequestDto = getCreateBookRequestDto();
        Book book = getBook();
        Long bookId = book.getId();

        BookDto expected = getBookDtoFromBook(book);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toEntity(createBookRequestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.update(bookId, createBookRequestDto);

        Assertions.assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookMapper, times(1)).toEntity(createBookRequestDto);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Update book by invalid ID - throws EntityNotFoundException")
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
    @DisplayName("Find books by invalid category ID - throws EntityNotFoundException")
    public void findByCategoryId_withIncorrectCategoryId_throwException() {
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(bookRepository.findAllByCategoryId(categoryId, pageable)).thenReturn(Page.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookService.findByCategoryId(categoryId, pageable));

        Assertions.assertEquals("Can't find books with category id: "
                + categoryId, exception.getMessage());
        verify(bookRepository, times(1)).findAllByCategoryId(categoryId, pageable);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Find books by valid category ID - returns list of books")
    public void findByCategoryId_withCorrectCategoryId_returnsListOfBooks() {
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        BookDtoWithoutCategoryIds bookDto = new BookDtoWithoutCategoryIds(
                1L, "Title", "Author", "1234567890", BigDecimal.valueOf(19.99),
                "Description", "image.jpg"
        );
        List<BookDtoWithoutCategoryIds> expectedBooks = List.of(bookDto);

        when(bookRepository.findAllByCategoryId(categoryId, pageable))
                .thenReturn(new PageImpl<>(List.of(new Book())));
        when(bookMapper.toDtoWithoutCategoryIds(any(Book.class))).thenReturn(bookDto);

        List<BookDtoWithoutCategoryIds> result = bookService.findByCategoryId(categoryId, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).title()).isEqualTo("Title");

        verify(bookRepository, times(1)).findAllByCategoryId(categoryId, pageable);
        verify(bookMapper, times(1)).toDtoWithoutCategoryIds(any(Book.class));
        verifyNoMoreInteractions(bookRepository);
    }

    private static Book getBook() {
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

    private static Category getCategory(Long categoryId) {
        Category category = new Category();
        category.setId(categoryId);
        category.setName("category");
        category.setDescription(DESCRIPTION);
        category.setDeleted(false);
        return category;
    }

    private static BookDto getBookDtoFromBook(Book book) {
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

    private static BookDtoWithoutCategoryIds getBookDtoWithoutCategoryIdsFromBook(Book book) {
        return new BookDtoWithoutCategoryIds(
                book.getId(), book.getTitle(), book.getAuthor(),
                book.getIsbn(), book.getPrice(), book.getDescription(),
                book.getCoverImage()
        );
    }

    private static CreateBookRequestDto getCreateBookRequestDto() {
        return new CreateBookRequestDto(
                TITLE, AUTHOR, ISBN, PRICE, DESCRIPTION, COVER_IMAGE, CATEGORIES_ID
        );
    }
}
