package com.example.librarymanagement.model.dto.request.auth;

import com.example.librarymanagement.validation.ValidPassword;
import com.example.librarymanagement.validation.ValidPasswordValueMatch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidPasswordValueMatch.List({
        @ValidPasswordValueMatch(
                field = "newPassword",
                fieldMatch = "confirmNewPassword",
                oldField = "oldPassword",
                message = "Passwords do not match."
        )
})
public class ChangePasswordRequest {
    @ValidPassword
    private String oldPassword;
    @ValidPassword
    private String newPassword;
    @ValidPassword
    private String confirmNewPassword;

}