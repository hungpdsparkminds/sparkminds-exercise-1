package com.example.librarymanagement.model.entity.book;

import com.example.librarymanagement.model.entity.AbstractAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Author extends AbstractAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(name = "deleted",
            nullable = false,
            columnDefinition = "boolean default false")
    private Boolean isDeleted;


    @OneToMany(mappedBy = "author")
    private List<Book> books;
}