package com.example.librarymanagement.service.auth;

import com.example.librarymanagement.model.entity.auth.User;

import java.io.IOException;

public interface UserService {
    User updateUser2FA(boolean use2FA) throws IOException;

    default String generateQRUrl(User user, String applicationName) {
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                applicationName, user.getEmail(), user.getSecret(), applicationName);
    }
}
