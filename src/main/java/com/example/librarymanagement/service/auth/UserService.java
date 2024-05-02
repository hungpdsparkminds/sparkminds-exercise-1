package com.example.librarymanagement.service.auth;

import com.example.librarymanagement.model.dto.request.auth.ChangeEmailRequest;
import com.example.librarymanagement.model.dto.request.auth.ChangePasswordRequest;
import com.example.librarymanagement.model.entity.auth.User;
import com.google.zxing.WriterException;

import java.io.IOException;

public interface UserService {
    void updateUser2FA(boolean use2FA);

    default String generateQRUrl(User user, String applicationName) {
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                applicationName, user.getEmail(), user.getSecret(), applicationName);
    }

    void changePassword(ChangePasswordRequest changePasswordRequest);

    String getUser2FA();

    void changeEmail(ChangeEmailRequest changeEmailRequest);

    void updateEmail(ChangeEmailRequest changeEmailRequest);

    void updatePhone(String phone);

    void verifyPhone(String otp);
}
