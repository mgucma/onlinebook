package com.marek.onlinebookstore.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marek.onlinebookstore.dto.book.BookDto;
import com.marek.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.marek.onlinebookstore.dto.book.CreateBookRequestDto;
import com.marek.onlinebookstore.model.Book;
import com.marek.onlinebookstore.model.Category;
import com.marek.onlinebookstore.service.book.BookService;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    public static final String TITLE = "Title";
    public static final String AUTHOR = "author";
    public static final String ISBN = "1231231";
    public static final BigDecimal PRICE = BigDecimal.valueOf(123.12);
    public static final String DESCRIPTION = "ok book";
    public static final String COVER_IMAGE = "image";
    public static final Set<Long> CATEGORIES_ID = Set.of();
    public static final Set<Category> CATEGORIES = Set.of();
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookService bookService;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("db/books/controller/delete-book-from-books.sql")
            );
        }
    }

    @WithMockUser(username = "user", roles = {"ADMIN"})
    @Test
    public void createBook_ValidCreateBookRequestDto_Success() throws Exception {
        //Given
        CreateBookRequestDto createBookRequestDto = getCreateBookRequestDto();
        Book book = getBook();
        BookDtoWithoutCategoryIds expected = getBookDtoWithoutCategoryIdsFromBook(book);

        String jsonRequest = objectMapper.writeValueAsString(createBookRequestDto);
        //When
        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //Then
        BookDtoWithoutCategoryIds actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDtoWithoutCategoryIds.class
        );
        Assertions.assertAll(() -> Assertions.assertNotNull(actual),
                () -> Assertions.assertNotNull(actual.id()),
                () -> Assertions.assertEquals(expected.title(), actual.title()),
                () -> Assertions.assertEquals(expected.author(), actual.author()),
                () -> Assertions.assertEquals(expected.isbn(), actual.isbn()),
                () -> Assertions.assertEquals(expected.isbn(), actual.isbn()),
                () -> Assertions.assertEquals(expected.price(), actual.price()),
                () -> Assertions.assertEquals(expected.description(), actual.description()),
                () -> Assertions.assertEquals(expected.coverImage(), actual.coverImage())
        );

        EqualsBuilder.reflectionEquals(expected, actual, "id", "cover_images");
    }

    @WithMockUser(username = "user", roles = {"ADMIN"})
    @Test
    public void createBook_ValidCreateBookRequestDto_Fail() throws Exception {
        //Given
        CreateBookRequestDto createBookRequestDto = null;

        String jsonRequest = objectMapper.writeValueAsString(createBookRequestDto);
        //When
        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        Exception exception = Assertions.assertThrows(NullPointerException.class,
                () -> bookService.save(createBookRequestDto));
        //Then
        Assertions.assertNotNull(exception);
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test

    public void getAll_ValidGetAllRequestDto_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        MvcResult result = mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> bookDtoList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, BookDto.class));
        Assertions.assertNotNull(bookDtoList);
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    public void getBookById_ValidId_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto bookDto = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);

        Assertions.assertNotNull(bookDto);
        Assertions.assertEquals(1L, bookDto.getId());
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    public void deleteBook_ValidId_Success() throws Exception {
        mockMvc.perform(delete("/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
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
        BookDtoWithoutCategoryIds withoutCategoryIds = new BookDtoWithoutCategoryIds(
                book.getId(), book.getTitle(), book.getAuthor(),
                book.getIsbn(), book.getPrice(), book.getDescription(),
                book.getCoverImage()
        );
        return withoutCategoryIds;
    }

    private static @NotNull CreateBookRequestDto getCreateBookRequestDto() {
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto(
                TITLE, AUTHOR, ISBN, PRICE, DESCRIPTION, COVER_IMAGE, CATEGORIES_ID
        );
        return createBookRequestDto;
    }

}
