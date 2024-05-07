package com.example.librarymanagement.model.entity.book;

import com.example.librarymanagement.model.entity.AbstractAuditEntity;
import com.example.librarymanagement.model.entity.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Reserve extends AbstractAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserve_id", nullable = false)
    private Long reserveId;

    @Column(name = "reserve_date", nullable = false)
    private OffsetDateTime reserveDate;

    @Column(name = "return_date", nullable = false)
    private OffsetDateTime returnDate;

    @Column(name = "due_date", nullable = false)
    private OffsetDateTime dueDate;

    @Column(name = "total")
    private Double total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReserveStatus reserveStatus;

    @ManyToOne
    @PrimaryKeyJoinColumn
    private User user;

    @OneToMany(mappedBy = "reserve", orphanRemoval = true)
    List<ReserveDetail> reserveDetails;
}
