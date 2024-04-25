package com.example.librarymanagement.model.entity.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_session")
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "session_id"
    )
    private UUID sessionId;

    @CreationTimestamp
    private OffsetDateTime createdOn;

    @Column(nullable = false, name = "expiration_time")
    private OffsetDateTime expirationTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserSessionStatus status;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "user_id")
    private User user;
}
