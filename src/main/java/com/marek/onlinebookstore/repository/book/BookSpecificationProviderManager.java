package com.marek.onlinebookstore.repository.book;

import com.marek.onlinebookstore.exception.ProviderException;
import com.marek.onlinebookstore.model.Book;
import com.marek.onlinebookstore.repository.SpecificationProvider;
import com.marek.onlinebookstore.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private final List<SpecificationProvider<Book>> specificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return specificationProviders.stream()
                .filter(b -> b.getKey().equals(key))
                .findFirst().orElseThrow(
                        () -> new ProviderException(
                                "Could not find specification provider for key: " + key)
                );
    }
}
