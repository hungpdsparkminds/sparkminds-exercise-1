package com.example.librarymanagement.repository.auth;

import com.example.librarymanagement.model.entity.auth.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
}
