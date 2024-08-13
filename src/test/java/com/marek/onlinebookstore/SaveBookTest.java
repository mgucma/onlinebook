package com.marek.onlinebookstore;

import com.marek.onlinebookstore.entity.Book;
import com.marek.onlinebookstore.repository.BookRepository;
import com.marek.onlinebookstore.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

class SaveBookTest {
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave() {
        Book book = new Book();
        book.setTitle("Test Book");
        bookService.save(book);
        verify(bookRepository, times(1)).save(book);
    }
}
