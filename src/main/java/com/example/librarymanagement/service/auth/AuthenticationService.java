package com.example.librarymanagement.service.auth;

import com.example.librarymanagement.model.dto.request.auth.ForgotPasswordRequest;
import com.example.librarymanagement.model.dto.request.auth.LoginRequest;
import com.example.librarymanagement.model.dto.request.auth.RegisterRequest;
import com.example.librarymanagement.model.dto.request.auth.ResetPasswordRequest;
import com.example.librarymanagement.model.dto.response.auth.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse login(LoginRequest loginRequest);

    void register(RegisterRequest registerRequest);

    void verifyEmailToken(String token);

    void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

    void resetPassword(ResetPasswordRequest resetPasswordRequest);
}
