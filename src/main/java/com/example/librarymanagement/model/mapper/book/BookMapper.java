package com.example.librarymanagement.model.mapper.book;

import com.example.librarymanagement.model.dto.request.book.BookPostRequest;
import com.example.librarymanagement.model.dto.response.book.BookResponse;
import com.example.librarymanagement.model.entity.book.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mapping(source = "categoryId", target = "category.categoryId")
    @Mapping(source = "authorId", target = "author.authorId")
    @Mapping(source = "publisherId", target = "publisher.publisherId")
    @Mapping(target = "isbn", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "coverImage", ignore = true)
    Book toBookEntity (BookPostRequest bookPostRequest);


    @Mapping(source = "author.books", target = "author.books", ignore = true)
    @Mapping(source = "publisher.books", target = "publisher.books", ignore = true)
    @Mapping(source = "category.books", target = "category.books", ignore = true)
    BookResponse toBookResponse(Book book);
}
