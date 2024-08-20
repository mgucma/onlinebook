package com.marek.onlinebookstore.mapper;

import com.marek.onlinebookstore.config.MapperConfig;
import com.marek.onlinebookstore.dto.BookDto;
import com.marek.onlinebookstore.dto.CreateBookRequestDto;
import com.marek.onlinebookstore.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BooksMapper {
    Book toModel(CreateBookRequestDto requestDto);

    BookDto toBookDto(Book book);

}
