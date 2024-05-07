package com.example.librarymanagement.model.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyEmailByOtpRequest {
    @Email(message = "invalid email")
    private String email;
    @NotBlank
    private String otp;
}
