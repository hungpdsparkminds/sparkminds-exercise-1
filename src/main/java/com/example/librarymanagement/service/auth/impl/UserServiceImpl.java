package com.example.librarymanagement.service.auth.impl;

import com.amazonaws.AmazonServiceException;
import com.example.librarymanagement.model.dto.request.auth.ChangeEmailRequest;
import com.example.librarymanagement.model.dto.request.auth.ChangePasswordRequest;
import com.example.librarymanagement.model.entity.auth.*;
import com.example.librarymanagement.model.exception.DataIntegrityViolationException;
import com.example.librarymanagement.model.exception.NotFoundException;
import com.example.librarymanagement.model.exception.VerificationException;
import com.example.librarymanagement.repository.auth.ChangeEmailRequestRepository;
import com.example.librarymanagement.repository.auth.TokenRepository;
import com.example.librarymanagement.repository.auth.UserRepository;
import com.example.librarymanagement.repository.auth.UserSessionRepository;
import com.example.librarymanagement.service.EmailService;
import com.example.librarymanagement.service.FileService;
import com.example.librarymanagement.service.SmsService;
import com.example.librarymanagement.service.auth.UserService;
import com.example.librarymanagement.utils.AuthUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.glxn.qrgen.QRCode;
import net.logstash.logback.util.StringUtils;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.example.librarymanagement.utils.Constants.ERROR_CODE.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtils authUtils;
    private final FileService fileService;
    private final ChangeEmailRequestRepository changeEmailRequestRepository;
    private final TokenRepository tokenRepository;
    private final UserSessionRepository userSessionRepository;
    private final SmsService smsService;
    private final EmailService emailService;
    @Value("${application.name}")
    private String applicationName;
    @Value("${application.security.jwt.access-token.expiration}")
    private long otpExpiration;


    @Override
    public void updateUser2FA(boolean use2FA) {
        var authenticatedUser = authUtils.getAuthenticatedUser();
        if (authenticatedUser.isUsing2FA() == use2FA) {
            return;
        }
        authenticatedUser.setUsing2FA(use2FA);
        userRepository.save(authenticatedUser);
        if (use2FA && StringUtils.isEmpty(authenticatedUser.getTotpQr())) {
            CompletableFuture.runAsync(() -> {
                authenticatedUser.setSecret(Base32.random());
                var content = this.generateQRUrl(authenticatedUser, applicationName);
                ByteArrayOutputStream qrCodeStream = QRCode.from(content)
                        .withSize(300, 300)
                        .stream();
                try {
                    authenticatedUser.setTotpQr(
                            fileService.uploadFile(qrCodeStream.toByteArray(),
                                    authenticatedUser.getUserId() + "_" + authenticatedUser.getEmail() + ".png",
                                    "totp/img/"));
                    userRepository.save(authenticatedUser);
                } catch (IOException e) {
                    throw new AmazonServiceException(AWS_S3_UPLOAD_OBJECT_ERROR);
                }
            });
        }
    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        var authenticatedUser = authUtils.getAuthenticatedUser();
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(),
                authenticatedUser.getPasswordHash())) {
            throw new VerificationException(EMAIL_WITH_PASSWORD_INCORRECT);
        }
        authenticatedUser.setPasswordHash(
                passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(authenticatedUser);
    }

    @Override
    public String getUser2FA() {
        var authenticatedUser = authUtils.getAuthenticatedUser();
        String totpQr = authenticatedUser.getTotpQr();
        if (StringUtils.isEmpty(authenticatedUser.getTotpQr())) {
            throw new NotFoundException(VERIFICATION_QR_NOT_FOUND);
        }
        return totpQr;
    }

    @Override
    @Transactional
    public void changeEmail(ChangeEmailRequest changeEmailRequest) {
        var authenticatedUser = authUtils.getAuthenticatedUser();
        userRepository.getUserByEmailEquals(changeEmailRequest.getEmail())
                .ifPresent((element) -> {
                    throw new DataIntegrityViolationException(EMAIL_EXISTED);
                });
        var expirationTime = OffsetDateTime.now().plus(Duration.ofMillis(otpExpiration));
        var tokenFrom = tokenRepository.save(Token.builder()
                .user(authenticatedUser)
                .value(String.format("%06d", (new SecureRandom()).nextInt(999999)))
                .tokenType(TokenType.EMAIL_CHANGE_OTP)
                .expirationTime(expirationTime)
                .status(TokenStatus.VALID)
                .build());
        var tokenTo = tokenRepository.save(Token.builder()
                .user(authenticatedUser)
                .value(String.format("%06d", (new SecureRandom()).nextInt(999999)))
                .tokenType(TokenType.EMAIL_CHANGE_OTP)
                .expirationTime(expirationTime)
                .status(TokenStatus.VALID)
                .build());
        changeEmailRequestRepository.save(ChangeEmail.builder()
                .user(authenticatedUser)
                .emailFrom(authenticatedUser.getEmail())
                .emailTo(changeEmailRequest.getEmail())
                .tokenFrom(tokenFrom)
                .tokenTo(tokenTo)
                .expirationTime(expirationTime)
                .status(ChangeEmailStatus.VALID)
                .build());
        CompletableFuture.runAsync(() -> revokeAllValidEmailVerificationTokens(authenticatedUser));
        CompletableFuture.runAsync(() -> {
            try {
                emailService.sendChangeEmailToken(authenticatedUser.getEmail(), tokenFrom.getValue());
            } catch (MessagingException e) {
                log.error("Error in Email Service: Messages:{}", e.getMessage());
            }
        });
        CompletableFuture.runAsync(() -> {
            try {
                emailService.sendChangeEmailToken(changeEmailRequest.getEmail(), tokenTo.getValue());
            } catch (MessagingException e) {
                log.error("Error in Email Service: Messages:{}", e.getMessage());
            }
        });
    }

    @Override
    @Transactional
    public void updateEmail(ChangeEmailRequest changeEmailRequest) {
        var authenticatedUser = authUtils.getAuthenticatedUser();
        changeEmailRequestRepository.findValidTokenByEmailFromEqualsAndEmailToEquals(
                authenticatedUser.getEmail(),
                changeEmailRequest.getEmail(),
                changeEmailRequest.getVerificationCodeFrom(),
                changeEmailRequest.getVerificationCodeTo()
        ).ifPresentOrElse(
                (element) -> {
                    List<ChangeEmail> changeEmailRequestsByUserEquals =
                            changeEmailRequestRepository.findAllByUserEqualsAndStatusEqualsAndExpirationTimeIsAfter(
                                    authenticatedUser, ChangeEmailStatus.VALID, OffsetDateTime.now());
                    List<ChangeEmail> changeEmailRequests = changeEmailRequestsByUserEquals.stream()
                            .map((c) -> {
                                c.setStatus(ChangeEmailStatus.REVOKED);
                                return c;
                            }).toList();
                    changeEmailRequestRepository.saveAll(changeEmailRequests);
                    element.setStatus(ChangeEmailStatus.VALID);
                    changeEmailRequestRepository.save(element);
                    List<UserSession> userSessions = userSessionRepository
                            .findAllByUserEqualsAndStatusEqualsAndExpirationTimeIsAfter(
                                    authenticatedUser, UserSessionStatus.ACTIVE, OffsetDateTime.now())
                            .stream()
                            .map((s) -> {
                                s.setStatus(UserSessionStatus.REVOKED);
                                return s;
                            }).toList();
                    userSessionRepository.saveAll(userSessions);
                    authenticatedUser.setEmail(changeEmailRequest.getEmail());
                    userRepository.save(authenticatedUser);
                },
                () -> {
                    throw new VerificationException(VERIFICATION_TOKEN_INVALID);
                }
        );
    }

    @Override
    @Transactional
    public void updatePhone(String phone) {
        var authenticatedUser = authUtils.getAuthenticatedUser();
        if (phone.equals(authenticatedUser.getPhone())) {
            return;
        }
        authenticatedUser.setPhoneVerified(false);
        var otp = String.format("%06d", (new SecureRandom()).nextInt(999999));
        tokenRepository.save(Token.builder()
                .user(authenticatedUser)
                .value(otp)
                .tokenType(TokenType.OTP)
                .expirationTime(OffsetDateTime.now().plus(Duration.ofMillis(otpExpiration)))
                .status(TokenStatus.VALID)
                .build());
        authenticatedUser.setPhoneVerified(false);
        authenticatedUser.setPhone(phone);
        userRepository.save(authenticatedUser);
//        smsService.send6DigitCode(phone, otp);
    }

    @Override
    @Transactional
    public void verifyPhone(String otp) {
        var authenticatedUser = authUtils.getAuthenticatedUser();
        tokenRepository.findByValueEqualsAndTokenTypeEqualsAndStatusEqualsAndUserEqualsAndExpirationTimeIsAfter(
                otp, TokenType.OTP, TokenStatus.VALID, authenticatedUser, OffsetDateTime.now()
        ).ifPresentOrElse((element) -> {
                    element.setStatus(TokenStatus.REVOKED);
                    tokenRepository.save(element);
                    authenticatedUser.setPhoneVerified(true);
                    userRepository.save(authenticatedUser);
                },
                () -> {
                    throw new VerificationException(TOKEN_NOT_FOUND);
                });
    }

    @Override
    public void revokeAllValidEmailVerificationTokens(User user) {
        List<Token> validEmailVerificationTokens = tokenRepository.findAllByTokenTypeEqualsAndStatusEqualsAndUserEqualsAndExpirationTimeIsAfter(
                TokenType.EMAIL_VERIFICATION, TokenStatus.VALID, user, OffsetDateTime.now()
        );
        validEmailVerificationTokens.forEach((element)->{
            element.setStatus(TokenStatus.REVOKED);
        });
        tokenRepository.saveAll(validEmailVerificationTokens);
    }
}
