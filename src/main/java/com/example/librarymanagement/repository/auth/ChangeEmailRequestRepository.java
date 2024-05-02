package com.example.librarymanagement.repository.auth;

import com.example.librarymanagement.model.entity.auth.ChangeEmail;
import com.example.librarymanagement.model.entity.auth.ChangeEmailStatus;
import com.example.librarymanagement.model.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChangeEmailRequestRepository extends JpaRepository<ChangeEmail, Long> {
    @Query("""
            SELECT c FROM ChangeEmail c
            JOIN Token tf ON tf.tokenId = c.tokenFrom.tokenId
            JOIN Token tt ON tt.tokenId = c.tokenTo.tokenId
            WHERE c.emailFrom=:emailFrom AND c.emailTo=:emailTo and c.status='VALID' AND c.expirationTime >= offset_datetime 
            AND tf.value= :tokenFrom AND tf.status='VALID' AND tf.tokenType='EMAIL_CHANGE_OTP' 
            AND tt.value= :tokenTo AND tt.status='VALID' AND tt.tokenType='EMAIL_CHANGE_OTP'
            """)
    Optional<ChangeEmail> findValidTokenByEmailFromEqualsAndEmailToEquals(String emailFrom, String emailTo, String tokenFrom, String tokenTo);

    List<ChangeEmail> findAllByUserEqualsAndStatusEqualsAndExpirationTimeIsAfter(User user, ChangeEmailStatus changeEmailStatus, OffsetDateTime offsetDateTime);
}
