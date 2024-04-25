package com.example.librarymanagement.service.auth.impl;

import com.example.librarymanagement.config.security.JwtService;
import com.example.librarymanagement.model.dto.request.auth.LoginRequest;
import com.example.librarymanagement.model.dto.response.auth.AuthenticationResponse;
import com.example.librarymanagement.model.entity.auth.User;
import com.example.librarymanagement.model.entity.auth.UserSession;
import com.example.librarymanagement.model.entity.auth.UserSessionStatus;
import com.example.librarymanagement.model.entity.auth.UserStatus;
import com.example.librarymanagement.model.exception.AccessDeniedException;
import com.example.librarymanagement.model.exception.NotFoundException;
import com.example.librarymanagement.repository.auth.RoleRepository;
import com.example.librarymanagement.repository.auth.TokenRepository;
import com.example.librarymanagement.repository.auth.UserRepository;
import com.example.librarymanagement.repository.auth.UserSessionRepository;
import com.example.librarymanagement.service.auth.AuthenticationService;
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
import redis.clients.jedis.JedisPooled;

import java.time.Duration;
import java.time.OffsetDateTime;

import static com.example.librarymanagement.utils.Constants.ERROR_CODE.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final String LOGIN_FAILURE_PREFIX = "LOGIN_FAILURE-";
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final RoleRepository roleRepository;
    //    private final EmailService emailService;
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
    public AuthenticationResponse login(LoginRequest loginRequest) {
        int loginAttempts = getLoginAttempts(loginRequest.getEmail());
        if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
            throw new AccessDeniedException(USER_BLOCKED);
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (Exception ex) {
            incrementLoginAttempts(loginRequest.getEmail(), loginAttempts + 1);
            throw new NotFoundException(EMAIL_AND_PASSWORD_INCORRECT);
        }
        var user = userRepository.getUserByEmailEquals(loginRequest.getEmail())
                .orElseThrow(() -> new NotFoundException(USER_WITH_EMAIL_NOT_FOUND));
        if (user.getStatus().equals(UserStatus.PENDING)) {
            throw new AccessDeniedException(USER_NOT_VERIFIED);
        }
        if (user.isUsing2FA()) {
            validate2FA(user, loginRequest.getVerificationCode(), loginAttempts + 1);
        }
        resetLoginAttempts(loginRequest.getEmail());
        return getAuthenticationResponse(user);
    }

    public AuthenticationResponse getAuthenticationResponse(User user) {
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

    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (final NumberFormatException e) {
            return false;
        }
        return true;
    }

    private void validate2FA(User user, String verificationCode, int attempts) {
        Totp totp = new Totp(user.getSecret());
        if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
            incrementLoginAttempts(user.getEmail(), attempts);
            throw new BadCredentialsException("Invalid Authenticator verification code");
        }
    }

    private void resetLoginAttempts(String email) {
        redisPool.del(LOGIN_FAILURE_PREFIX + email);
    }

    private int getLoginAttempts(String email) {
        String attempts = redisPool.get(LOGIN_FAILURE_PREFIX + email);
        return attempts == null ? 0 : Integer.parseInt(attempts);
    }

    private void incrementLoginAttempts(String email, int attempts) {
        redisPool.setex(LOGIN_FAILURE_PREFIX + email,
                blockExpirationInMinutes,
                String.valueOf(attempts));
    }
}
