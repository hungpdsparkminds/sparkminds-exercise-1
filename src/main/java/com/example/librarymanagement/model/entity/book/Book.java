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
public class Book  extends AbstractAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long isbn;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column
    private String detail;

    @Column(name = "no_copies")
    private Integer noCopies;

    @Column(name = "in_stock")
    private Integer inStock;

    @Column(name = "cover_image")
    private String coverImage;

    @Column
    private Double price;

    @Column(name = "deleted",
            nullable = false,
            columnDefinition = "boolean default false")
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private  Author author;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @OneToMany(mappedBy = "book")
    private List<ReserveDetail> reserveDetails;
}
