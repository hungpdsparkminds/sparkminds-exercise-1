package com.example.librarymanagement.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendVerificationEmail(String email, String token, String otp) throws MessagingException;

    void sendForgotPassword(String email, String newPassword) throws MessagingException;
    void sendChangeEmailToken(String email, String token) throws MessagingException;
}
