package com.marek.onlinebookstore.repository.book;

import com.marek.onlinebookstore.dto.book.BookSearchParametersDto;
import com.marek.onlinebookstore.model.Book;
import com.marek.onlinebookstore.repository.SpecificationBuilder;
import com.marek.onlinebookstore.repository.SpecificationProviderManager;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParametersDto) {
        Specification<Book> specification = Specification.where(null);
        Map<String, String[]> parametersMap = new HashMap<>();
        parametersMap.put("title", searchParametersDto.title());
        parametersMap.put("author", searchParametersDto.author());
        parametersMap.put("isbn", searchParametersDto.isbn());
        for (Map.Entry<String, String[]> entry : parametersMap.entrySet()) {
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                specification = specification.and(specificationProviderManager
                        .getSpecificationProvider(entry.getKey())
                        .getSpecification(values));
            }
        }
        return specification;
    }
}
