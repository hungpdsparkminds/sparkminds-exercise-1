package com.example.librarymanagement.model.dto.request.book;

import com.example.librarymanagement.validation.ValidFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookPostRequest {
    private String title;
    private String detail;
    private String edition;
    private Integer noCopies;
    private Integer stock = noCopies;
    private Long categoryId;
    private Long authorId;
    private Long publisherId;
    private Double price;
    @ValidFile
    private MultipartFile image;
}
