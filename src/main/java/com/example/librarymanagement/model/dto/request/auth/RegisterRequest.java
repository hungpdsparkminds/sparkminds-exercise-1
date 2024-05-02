package com.example.librarymanagement.model.dto.request.auth;

import com.example.librarymanagement.validation.ValidPassword;
import com.example.librarymanagement.validation.ValidPasswordValueMatch;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidPasswordValueMatch.List({
        @ValidPasswordValueMatch(
                field = "password",
                fieldMatch = "confirmPassword",
                message = "Passwords do not match!",
                oldField = ""
        )
})
public class RegisterRequest {
    @Email(message = "invalid email")
    private String email;
    @ValidPassword
    private String password;
    @ValidPassword
    private String confirmPassword;
}
