package com.example.librarymanagement.service.book;

import com.example.librarymanagement.model.dto.request.book.BookPostRequest;
import com.example.librarymanagement.model.dto.response.book.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tech.jhipster.service.Criteria;

import java.io.IOException;

public interface BookService {
    void add(BookPostRequest bookPostRequest) throws IOException;

    void softDeleteById(Long isbn);

    Page<BookResponse> findAllByFilter(Criteria criteria, Pageable pageable);
}
