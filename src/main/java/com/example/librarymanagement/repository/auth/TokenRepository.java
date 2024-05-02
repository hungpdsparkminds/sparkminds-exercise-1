package com.example.librarymanagement.repository.auth;

import com.example.librarymanagement.model.entity.auth.Token;
import com.example.librarymanagement.model.entity.auth.TokenStatus;
import com.example.librarymanagement.model.entity.auth.TokenType;
import com.example.librarymanagement.model.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByValueEquals(String value);

    @Query(value = """
                SELECT t FROM Token t
                INNER JOIN User u ON t.user.userId = u.userId
                WHERE u.userId = :userId AND t.status = 'VALID' AND t.expirationTime >= offset_datetime
            """)
    List<Token> findAllValidTokenByUser(Long userId);

    Optional<Token> findByValueEqualsAndTokenTypeEqualsAndStatusEqualsAndUserEqualsAndExpirationTimeIsAfter
            (String value, TokenType type, TokenStatus tokenStatus, User user, OffsetDateTime offsetDateTime);

}
