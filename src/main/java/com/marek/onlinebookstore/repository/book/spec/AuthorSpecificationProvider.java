package com.marek.onlinebookstore.repository.book.spec;

import com.marek.onlinebookstore.model.Book;
import com.marek.onlinebookstore.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    private static final String FIELD_NAME = "author";

    @Override
    public String getKey() {
        return FIELD_NAME;
    }

    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get(FIELD_NAME)
                .in(Arrays.stream(params).toArray());
    }
}
