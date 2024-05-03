package com.example.librarymanagement.model.entity.auth;

import com.example.librarymanagement.model.entity.AbstractAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "change_email_request")
public class ChangeEmail extends AbstractAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "change_email_request_id"
    )
    private Long changeEmailRequestId;

    @NotBlank(message = "Email must be specified.")
    @Email(message = "Email must be valid.")
    @Column(name = "email_from")
    private String emailFrom;

    @NotBlank(message = "Email to request change must be specified.")
    @Email(message = "Email to request change must be valid.")
    @Column(name = "email_to")
    private String emailTo;

    @Column(nullable = false, name = "expiration_time")
    private OffsetDateTime expirationTime;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "token_from_id", referencedColumnName = "token_id")
    private Token tokenFrom;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "token_to_id", referencedColumnName = "token_id")
    private Token tokenTo;

    @NotNull(message = "Change email request status must be specified.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChangeEmailStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
