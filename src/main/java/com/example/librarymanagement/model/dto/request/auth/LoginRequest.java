package com.example.librarymanagement.model.dto.request.auth;

import com.example.librarymanagement.validation.ValidEmail;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
//    @ValidEmail
    @Email(message = "invalid email")
    private String email;
    private String password;
    private String verificationCode;
}