package com.example.librarymanagement.repository.auth;

import com.example.librarymanagement.model.entity.auth.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByValueEquals(String value);

    @Query(value = """
                SELECT t FROM Token t
                INNER JOIN User u ON t.user.userId = u.userId
                WHERE u.userId = :userId AND t.status = 'VALID'
            """)
    List<Token> findAllValidTokenByUser(Long userId);

}
