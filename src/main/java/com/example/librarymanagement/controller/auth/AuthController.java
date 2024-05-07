package com.example.librarymanagement.controller.auth;

import com.example.librarymanagement.model.dto.request.auth.*;
import com.example.librarymanagement.model.dto.response.auth.AuthenticationResponse;
import com.example.librarymanagement.model.entity.auth.TokenType;
import com.example.librarymanagement.service.auth.AuthenticationService;
import com.example.librarymanagement.utils.AuthUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
@Slf4j
public class AuthController {
    private final AuthenticationService authenticationService;
    private final AuthUtils authUtils;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody @Valid LoginRequest loginRequest) {
        AuthenticationResponse response = authenticationService.login(loginRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        authUtils.generateSessionResponseCookies(response))
                .body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody @Valid RegisterRequest registerRequest) {
        authenticationService.register(registerRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@CookieValue(name = "refreshToken", defaultValue = "")
                                                                   String refreshToken) {
        AuthenticationResponse response = authenticationService.refreshToken(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        authUtils.generateSessionResponseCookies(response))
                .body(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        authenticationService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        authenticationService.resetPassword(resetPasswordRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        authenticationService.verifyEmailToken(token, TokenType.EMAIL_VERIFICATION);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmailOtp(@RequestBody @Valid VerifyEmailByOtpRequest verifyEmailByOtpRequest) {
        authenticationService.verifyEmailToken(verifyEmailByOtpRequest, TokenType.EMAIL_VERIFICATION_OTP);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/resend-verification-email")
    public ResponseEntity<Void> resendVerificationEmail(@RequestParam @Email @Valid String email) {
        authenticationService.resendVerifyEmail(email);
        return ResponseEntity.noContent().build();
    }

}
