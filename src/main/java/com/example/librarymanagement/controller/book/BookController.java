package com.example.librarymanagement.controller.book;

import com.example.librarymanagement.model.dto.request.book.BookPostRequest;
import com.example.librarymanagement.model.dto.response.book.BookResponse;
import com.example.librarymanagement.service.book.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.service.Criteria;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/books")
@Slf4j
public class BookController {
    private final BookService bookService;

    @PostMapping(consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> add(
            @ModelAttribute @Valid BookPostRequest bookPostRequest) throws IOException {
        bookService.add(bookPostRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public Page<BookResponse> getBooks(@RequestParam Criteria criteria, Pageable pageable){
        return bookService.findAllByFilter(criteria, pageable);
    }
    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> softDeleteById(@PathVariable Long isbn) {
        bookService.softDeleteById(isbn);
        return ResponseEntity.noContent().build();
    }

}
