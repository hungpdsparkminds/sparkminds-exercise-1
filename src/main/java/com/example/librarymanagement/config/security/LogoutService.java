package com.example.librarymanagement.config.security;

import com.example.librarymanagement.model.entity.auth.UserSessionStatus;
import com.example.librarymanagement.repository.auth.UserSessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final JwtService jwtService;
    private final UserSessionRepository userSessionRepository;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);
        var userSessionString = jwtService.extractUserSession(jwt);
        var userSession = userSessionRepository.findById(UUID.fromString(userSessionString))
                .orElse(null);
        if (userSession != null) {
            userSession.setStatus(UserSessionStatus.REVOKED);
            userSessionRepository.save(userSession);
            SecurityContextHolder.clearContext();
        }
    }
}