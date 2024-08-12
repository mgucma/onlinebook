package com.marek.onlinebookstore;

import com.marek.onlinebookstore.entity.Book;
import com.marek.onlinebookstore.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookRepositoryTests {

    @Autowired
    private BookRepository bookRepository;

    @Test
    void testCreateReadUpdateDelete() {
        // Create
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("1234567890");
        book.setPrice(BigDecimal.valueOf(19.99));
        book.setDescription("A book for testing purposes.");
        book.setCoverImage("cover.jpg");
        book.setIsDeleted(false);

        book = bookRepository.save(book);
        assertNotNull(book.getId());

        // Read
        Optional<Book> foundBook = bookRepository.findById(book.getId());
        assertTrue(foundBook.isPresent());
        assertEquals("Test Book", foundBook.get().getTitle());

        // Update
        book.setTitle("Updated Test Book");
        bookRepository.save(book);
        foundBook = bookRepository.findById(book.getId());
        assertTrue(foundBook.isPresent());
        assertEquals("Updated Test Book", foundBook.get().getTitle());

        // Delete
        bookRepository.delete(book);
        foundBook = bookRepository.findById(book.getId());
        assertFalse(foundBook.isPresent());
    }
}
