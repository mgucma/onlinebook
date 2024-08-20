package com.marek.onlinebookstore.repository;

import com.marek.onlinebookstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("UPDATE Book b SET b = :book WHERE b.id = :id")
    int updateBookById(Long id,Book book);
}
