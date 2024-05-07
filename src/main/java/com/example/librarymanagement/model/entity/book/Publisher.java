package com.example.librarymanagement.model.entity.book;

import com.example.librarymanagement.model.entity.AbstractAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
public class Publisher extends AbstractAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "publisher_id", nullable = false)
    private Long publisherId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column
    private String country;

    @Column(name = "founded_year")
    private String foundedYear;

    @Column(name = "deleted",
            nullable = false,
            columnDefinition = "boolean default false")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "publisher")
    private List<Book> books;
}