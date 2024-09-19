package com.marek.onlinebookstore.repository.book;

import com.marek.onlinebookstore.model.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

    private static final Long CATEGORY_ID = 1L;
    private static final int PAGE_SIZE = 10;
    private static final String EXPECTED_BOOK_TITLE = "Title";

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Find all books by category id")
    @Sql(scripts = {"classpath:db/books/repository/remove-book-from-books-table.sql",
            "classpath:db/books/repository/add-book-to-books-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/books/repository/remove-book-from-books-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoriesId_OneCategoryBook_returnOneBookInList() {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE);

        Page<Book> actualPage = bookRepository.findAllByCategoryId(CATEGORY_ID, pageable);
        Assertions.assertEquals(1, actualPage.getTotalElements());
        Assertions.assertEquals(EXPECTED_BOOK_TITLE, actualPage.getContent().get(0).getTitle());
    }
}
