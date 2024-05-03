package com.example.librarymanagement.model.dto.request.auth;

import com.example.librarymanagement.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @Email(message = "invalid email")
    private String email;
    @ValidPassword
    private String password;
    private String verificationCode;
}