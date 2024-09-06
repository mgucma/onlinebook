package com.marek.onlinebookstore.repository.book;

import com.marek.onlinebookstore.model.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @Query("SELECT b FROM Book b JOIN FETCH b.categories c WHERE c.id = :categoriesId")
    List<Book> findAllByCategoriesId(Long categoriesId, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN FETCH b.categories c WHERE c.id IS NOT NULL")
    Page<Book> findAllWithCategories(Pageable pageable);

    @Query("SELECT b FROM Book b JOIN FETCH b.categories c WHERE c.id IS NOT NULL AND b.id = :id")
    Optional<Book> findByIdWithCategories(Long id);
}
