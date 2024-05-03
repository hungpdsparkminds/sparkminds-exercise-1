package com.example.librarymanagement.model.dto.request.auth;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeEmailRequest {
    @Email(message = "invalid email")
    private String email;
    private String verificationCodeFrom;
    private String verificationCodeTo;
}