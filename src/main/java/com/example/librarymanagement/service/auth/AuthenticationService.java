package com.example.librarymanagement.service.auth;

import com.example.librarymanagement.model.dto.request.auth.*;
import com.example.librarymanagement.model.dto.response.auth.AuthenticationResponse;
import com.example.librarymanagement.model.entity.auth.TokenType;

public interface AuthenticationService {
    AuthenticationResponse login(LoginRequest loginRequest);

    void register(RegisterRequest registerRequest);

    void verifyEmailToken(String token, TokenType type);

    void verifyEmailToken(VerifyEmailByOtpRequest verifyEmailByOtpRequest, TokenType type);

    void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

    void resetPassword(ResetPasswordRequest resetPasswordRequest);

    void resendVerifyEmail(String email);

    AuthenticationResponse refreshToken(String refreshToken);
}
