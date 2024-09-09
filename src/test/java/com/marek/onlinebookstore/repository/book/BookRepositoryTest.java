package com.marek.onlinebookstore.repository.book;

import com.marek.onlinebookstore.model.Book;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("""
            Find all books by category id
            """)
    @Sql(scripts = {"classpath:db/books/repository/Add-book-to-books-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/books/repository/remove-book-from-books-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoriesId_OneCategoryBook_returnOneBookInList() {
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        List<Book> actual = bookRepository.findAllByCategoriesId(categoryId, pageable);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals("title",
                actual.get(0).getTitle());
    }
}
