package com.marek.onlinebookstore;

import com.marek.onlinebookstore.entity.Book;
import com.marek.onlinebookstore.service.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlineBookstoreApplication implements CommandLineRunner {

    private final BookService bookService;

    @Autowired
    public OnlineBookstoreApplication(BookService bookService) {
        this.bookService = bookService;
    }

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookstoreApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Book book1 = new Book();
        book1.setTitle("Effective Java");
        book1.setAuthor("Joshua Bloch");
        book1.setIsbn("9780134685991");
        book1.setPrice(new BigDecimal("45.00"));
        book1.setDescription("A comprehensive guide to programming in Java.");
        book1.setCoverImage("url/to/coverimage1");

        Book book2 = new Book();
        book2.setTitle("Clean Code");
        book2.setAuthor("Robert C. Martin");
        book2.setIsbn("9780132350884");
        book2.setPrice(new BigDecimal("50.00"));
        book2.setDescription("A Handbook of Agile Software Craftsmanship.");
        book2.setCoverImage("url/to/coverimage2");

        bookService.save(book1);
        bookService.save(book2);
    }
}

