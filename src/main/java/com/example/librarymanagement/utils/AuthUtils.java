package com.example.librarymanagement.utils;

import com.example.librarymanagement.model.dto.response.auth.AuthenticationResponse;
import com.example.librarymanagement.model.entity.auth.User;
import com.example.librarymanagement.model.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.example.librarymanagement.utils.Constants.ERROR_CODE.USER_NOT_FOUND;

@Component
public class AuthUtils {
    @Value("${application.security.jwt.access-token.expiration}")
    private long accessTokenExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    private AuthUtils() {
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User user) {
                return user;
            }
        }
        throw new NotFoundException(USER_NOT_FOUND);
    }

    private String createCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAge)
                .build()
                .toString();
    }

    public String[] generateSessionResponseCookies(AuthenticationResponse authenticationResponse) {
        return new String[]{
                createCookie("sessionId", authenticationResponse.getSessionId(), refreshExpiration),
                createCookie("refreshToken", authenticationResponse.getRefreshToken(), refreshExpiration),
                createCookie("accessToken", authenticationResponse.getAccessToken(), accessTokenExpiration)
        };
    }
}
