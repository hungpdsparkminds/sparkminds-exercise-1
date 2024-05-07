package com.example.librarymanagement.service.book.impl;

import com.example.librarymanagement.model.dto.request.book.BookPostRequest;
import com.example.librarymanagement.model.dto.response.book.BookResponse;
import com.example.librarymanagement.model.entity.book.Author;
import com.example.librarymanagement.model.entity.book.Category;
import com.example.librarymanagement.model.entity.book.Publisher;
import com.example.librarymanagement.model.exception.DataIntegrityViolationException;
import com.example.librarymanagement.model.exception.NotFoundException;
import com.example.librarymanagement.model.mapper.book.BookMapper;
import com.example.librarymanagement.repository.book.AuthorRepository;
import com.example.librarymanagement.repository.book.BookRepository;
import com.example.librarymanagement.repository.book.CategoryRepository;
import com.example.librarymanagement.repository.book.PublisherRepository;
import com.example.librarymanagement.service.FileService;
import com.example.librarymanagement.service.book.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tech.jhipster.service.Criteria;

import java.io.IOException;

import static com.example.librarymanagement.utils.Constants.ERROR_CODE.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final FileService fileService;

    @Override
    public void add(BookPostRequest bookPostRequest) throws IOException {
        Category category = categoryRepository.findById(bookPostRequest.getCategoryId())
                .orElseThrow(() -> new DataIntegrityViolationException(BOOK_CATEGORY_NOT_FOUND));
        Publisher publisher = publisherRepository.findById(bookPostRequest.getPublisherId())
                .orElseThrow(() -> new DataIntegrityViolationException(BOOK_PUBLISHER_NOT_FOUND));
        Author author = authorRepository.findById(bookPostRequest.getAuthorId())
                .orElseThrow(() -> new DataIntegrityViolationException(BOOK_AUTHOR_NOT_FOUND));
        var book = BookMapper.INSTANCE.toBookEntity(bookPostRequest);
        if (bookPostRequest.getImage() != null) {
            book.setCoverImage(fileService.uploadFile(bookPostRequest.getImage(), "book/img/"));
        }
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setCategory(category);
        bookRepository.save(book);
    }

    @Override
    public void softDeleteById(Long isbn) {
        bookRepository.findById(isbn)
                .ifPresentOrElse(
                        item -> {
                            item.setIsDeleted(true);
                            bookRepository.save(item);
                        },
                        () -> {
                            throw new NotFoundException(BOOK_NOT_FOUND);
                        }
                );
    }

    @Override
    public Page<BookResponse> findAllByFilter(Criteria criteria, Pageable pageable) {
        return null;
    }
}
