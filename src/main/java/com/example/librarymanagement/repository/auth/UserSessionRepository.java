package com.example.librarymanagement.repository.auth;

import com.example.librarymanagement.model.entity.auth.User;
import com.example.librarymanagement.model.entity.auth.UserSession;
import com.example.librarymanagement.model.entity.auth.UserSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
    List<UserSession> findAllByUserEqualsAndStatusEqualsAndExpirationTimeIsAfter(
            User user, UserSessionStatus userSessionStatus, OffsetDateTime offsetDateTime);
}
