package com.example.librarymanagement.model.entity.book;

import com.example.librarymanagement.model.entity.AbstractAuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reserve_detail")
public class ReserveDetail extends AbstractAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserve_detail_id", nullable = false)
    private Long reserveDetailId;

    @Column
    private Integer quantity;

    @Column
    private Double price;

    @ManyToOne
    @PrimaryKeyJoinColumn
    private Book book;

    @ManyToOne
    @PrimaryKeyJoinColumn
    private Reserve reserve;

}
