package com.marek.onlinebookstore.repository.book;

import com.marek.onlinebookstore.model.Book;
import com.marek.onlinebookstore.model.Category;
import com.marek.onlinebookstore.repository.category.CategoryRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

    private static final int PAGE_SIZE = 10;

    private static final String CATEGORY_NAME = "Test Category";
    private static final String BOOK_TITLE = "Test Book Title";
    private static final String BOOK_AUTHOR = "Test Author";
    private static final String BOOK_ISBN = "1234567890123";
    private static final BigDecimal BOOK_PRICE = new BigDecimal("19.99");

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @Transactional
    @Rollback
    @DisplayName("Find all books by category id")
    void findAllByCategoryId_OneCategoryBook_returnOneBookInList() {
        Category category = new Category();
        category.setName(CATEGORY_NAME);
        category = categoryRepository.save(category);

        Book book = new Book();
        book.setTitle(BOOK_TITLE);
        book.setAuthor(BOOK_AUTHOR);
        book.setIsbn(BOOK_ISBN);
        book.setPrice(BOOK_PRICE);

        // Associate the book with the category and save it
        book.getCategories().add(category);
        bookRepository.save(book);

        Long categoryId = category.getId();
        Pageable pageable = PageRequest.of(0, PAGE_SIZE);
        List<Book> actual = bookRepository.findAllByCategoryId(categoryId, pageable).getContent();

        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(BOOK_TITLE, actual.get(0).getTitle());
        Assertions.assertEquals(BOOK_AUTHOR, actual.get(0).getAuthor());
        Assertions.assertEquals(BOOK_ISBN, actual.get(0).getIsbn());
        Assertions.assertEquals(BOOK_PRICE, actual.get(0).getPrice());
    }
}
