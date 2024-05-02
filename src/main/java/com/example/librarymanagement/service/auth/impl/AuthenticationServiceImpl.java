package com.example.librarymanagement.service.auth.impl;

import com.example.librarymanagement.config.security.JwtService;
import com.example.librarymanagement.model.dto.request.auth.ForgotPasswordRequest;
import com.example.librarymanagement.model.dto.request.auth.LoginRequest;
import com.example.librarymanagement.model.dto.request.auth.RegisterRequest;
import com.example.librarymanagement.model.dto.request.auth.ResetPasswordRequest;
import com.example.librarymanagement.model.dto.response.auth.AuthenticationResponse;
import com.example.librarymanagement.model.entity.auth.*;
import com.example.librarymanagement.model.exception.AccessDeniedException;
import com.example.librarymanagement.model.exception.DataIntegrityViolationException;
import com.example.librarymanagement.model.exception.NotFoundException;
import com.example.librarymanagement.model.exception.VerificationException;
import com.example.librarymanagement.repository.auth.RoleRepository;
import com.example.librarymanagement.repository.auth.TokenRepository;
import com.example.librarymanagement.repository.auth.UserRepository;
import com.example.librarymanagement.repository.auth.UserSessionRepository;
import com.example.librarymanagement.service.EmailService;
import com.example.librarymanagement.service.auth.AuthenticationService;
import com.github.javafaker.Faker;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;
import redis.clients.jedis.JedisPooled;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import static com.example.librarymanagement.utils.Constants.ERROR_CODE.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final String LOGIN_FAILURE_PREFIX = "LOGIN_FAILURE-";
    private static final String USER_ROLE_NAME = "User";
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final JedisPooled redisPool;
    private final UserSessionRepository userSessionRepository;
    @Lazy
    private final AuthenticationManager authenticationManager;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.access-token.expiration}")
    private long accessTokenExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;
    @Value("${application.security.block-expiration}")
    private long blockExpirationInMinutes;

    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) {
        var role = roleRepository.findByNameEquals(USER_ROLE_NAME)
                .orElseThrow(() -> new NotFoundException(ROLE_NOT_FOUND));
        userRepository.getUserByEmailEquals(registerRequest.getEmail())
                .ifPresent(user -> {
                    throw new DataIntegrityViolationException(EMAIL_EXISTED);
                });
        var user = userRepository.save(User.builder()
                .email(registerRequest.getEmail())
                .role(role)
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .status(UserStatus.PENDING)
                .build());
        var token = tokenRepository.save(Token
                .builder()
                .user(user)
                .tokenType(TokenType.EMAIL_VERIFICATION)
                .expirationTime(OffsetDateTime.now().plusSeconds(jwtExpiration / 1000))
                .status(TokenStatus.VALID)
                .value(jwtService.generateToken(new HashMap<>(), user))
                .build());
        CompletableFuture.runAsync(() -> {
            try {
                emailService.sendVerificationEmail(user.getEmail(), token.getValue());
            } catch (MessagingException e) {
                log.error("Error in Email Service: Messages:{}", e.getMessage());
            }
        });
    }

    @Transactional
    public AuthenticationResponse login(LoginRequest loginRequest) {
        if (hasTooManyLoginAttempts(loginRequest.getEmail())) {
            throw new AccessDeniedException(USER_BLOCKED);
        }
        authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        var user = findUserByEmail(loginRequest.getEmail());
        if (user.getStatus().equals(UserStatus.PENDING)) {
            throw new AccessDeniedException(USER_NOT_VERIFIED);
        }
        if (user.getStatus().equals(UserStatus.NEED_CHANGE_PASSWORD)) {
            throw new AccessDeniedException(NEED_CHANGE_PASSWORD);
        }
        if (user.isUsing2FA()) {
            validate2FA(user, loginRequest.getVerificationCode());
        }
        resetLoginAttempts(loginRequest.getEmail());
        return generateAuthenticationResponse(user);
    }


    @Transactional
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        var user = findUserByEmail(forgotPasswordRequest.getEmail());
        var newPassword = generateRandomPassword();
        setUserPassword(user, newPassword);
        user.setStatus(UserStatus.NEED_CHANGE_PASSWORD);
        userRepository.save(user);
        CompletableFuture.runAsync(() -> {
            try {
                emailService.sendForgotPassword(user.getEmail(), newPassword);
            } catch (MessagingException e) {
                log.error("Error in Email Service: Messages:{}", e.getMessage());
            }
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        if (hasTooManyLoginAttempts(resetPasswordRequest.getEmail())) {
            throw new AccessDeniedException(USER_BLOCKED);
        }
        authenticate(resetPasswordRequest.getEmail(),
                resetPasswordRequest.getOldPassword());
        var user = findUserByEmail(resetPasswordRequest.getEmail());
        if (user.getStatus().equals(UserStatus.PENDING)) {
            throw new AccessDeniedException(USER_NOT_VERIFIED);
        }
        setUserPassword(user, resetPasswordRequest.getNewPassword());
        userRepository.save(user);
        resetLoginAttempts(resetPasswordRequest.getEmail());
    }

    @Transactional
    public void verifyEmailToken(String token) {
        var user = getUserFromToken(token);
        tokenRepository.findByValueEqualsAndTokenTypeEqualsAndStatusEqualsAndUserEqualsAndExpirationTimeIsAfter(
                        token, TokenType.EMAIL_VERIFICATION, TokenStatus.VALID, user, OffsetDateTime.now())
                .ifPresentOrElse(
                        element -> {
                            element.setStatus(TokenStatus.REVOKED);
                            tokenRepository.save(element);
                        },
                        () -> {
                            throw new NotFoundException(TOKEN_NOT_FOUND);
                        });
        if (user.getStatus().equals(UserStatus.PENDING)) {
            user.setStatus(UserStatus.ACTIVE);
        }
        markEmailVerified(user);
    }

    private boolean hasTooManyLoginAttempts(String email) {
        return getLoginAttempts(email) >= MAX_LOGIN_ATTEMPTS;
    }

    private int getLoginAttempts(String email) {
        var attempts = redisPool.get(LOGIN_FAILURE_PREFIX + email);
        return attempts == null ? 0 : Integer.parseInt(attempts);
    }

    private void resetLoginAttempts(String email) {
        redisPool.del(LOGIN_FAILURE_PREFIX + email);
    }

    private void authenticate(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (Exception ex) {
            incrementLoginAttempts(email);
            throw new NotFoundException(EMAIL_WITH_PASSWORD_INCORRECT);
        }
    }

    private void incrementLoginAttempts(String email) {
        var currentAttempts = getLoginAttempts(email);
        redisPool.setex(LOGIN_FAILURE_PREFIX + email,
                blockExpirationInMinutes,
                String.valueOf(currentAttempts + 1));
    }

    private User findUserByEmail(String email) {
        return userRepository.getUserByEmailEquals(email)
                .orElseThrow(() -> new NotFoundException(USER_WITH_EMAIL_NOT_FOUND));
    }

    private void setUserPassword(User user, String newPassword) {
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setStatus(UserStatus.ACTIVE);
    }

    private String generateRandomPassword() {
        return new Faker().internet().password(20, 30, true, true);
    }

    private AuthenticationResponse generateAuthenticationResponse(User user) {
        var userSession = userSessionRepository
                .save(UserSession.builder()
                        .user(user)
                        .expirationTime(OffsetDateTime.now().plus(Duration.ofMillis(refreshExpiration)))
                        .status(UserSessionStatus.ACTIVE).build());
        var accessToken = jwtService.generateAccessTokenWithSession(user, userSession.getSessionId().toString());
        var refreshToken = jwtService.generateRefreshTokenWithSession(user, userSession.getSessionId().toString());
        return AuthenticationResponse.builder()
                .sessionId(userSession.getSessionId().toString())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void markEmailVerified(User user) {
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    private User getUserFromToken(String token) {
        var userId = jwtService.extractUserId(token);
        if (StringUtils.isEmpty(userId)) {
            throw new VerificationException(VERIFICATION_TOKEN_INVALID);
        }
        var user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        if (!jwtService.isTokenValid(token, user)) {
            throw new VerificationException(VERIFICATION_TOKEN_INVALID);
        }
        return user;
    }

    private void validate2FA(User user, String verificationCode) {
        var totp = new Totp(user.getSecret());
        if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
            incrementLoginAttempts(user.getEmail());
            throw new BadCredentialsException("Invalid Authenticator verification code");
        }
    }

    private boolean isValidLong(String value) {
        try {
            Long.parseLong(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}
