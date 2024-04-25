package com.example.librarymanagement.model.entity.auth;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "token_id"
    )
    private Long tokenId;

    @NotNull(message = "Token type must be specified.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "token_type")
    private TokenType tokenType;

    @NotBlank(message = "Token value must be specified.")
    @Column(nullable = false)
    private String value;

    @CreationTimestamp
    private OffsetDateTime createdOn;

    @Column(nullable = false, name = "expiration_time")
    private OffsetDateTime expirationTime;

    @NotNull(message = "Token status must be specified.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}