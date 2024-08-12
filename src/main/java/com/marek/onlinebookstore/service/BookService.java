package com.marek.onlinebookstore.service;

import com.marek.onlinebookstore.entity.Book;
import java.util.List;

public interface BookService {
    void save(Book book);

    List<Book> findAll();
}
