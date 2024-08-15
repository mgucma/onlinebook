package com.marek.onlinebookstore.mapper;

import com.marek.onlinebookstore.config.MapperConfig;
import com.marek.onlinebookstore.dto.BookDto;
import com.marek.onlinebookstore.dto.CreateBookRequestDto;
import com.marek.onlinebookstore.entity.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BooksMapper {
    BookDto toBookDto(Book book);

    Book toEntity(CreateBookRequestDto createBookRequestDto);
}
