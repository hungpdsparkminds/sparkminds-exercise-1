package com.example.librarymanagement.model.dto.response.book;

import com.example.librarymanagement.model.entity.book.Author;
import com.example.librarymanagement.model.entity.book.Category;
import com.example.librarymanagement.model.entity.book.Publisher;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {
    private Long isbn;
    private String title;
    private String detail;
    private Integer noCopies;
    private Integer inStock;
    private String coverImage;
    private Double price;
    private Category category;
    private Author author;
    private Publisher publisher;
}
