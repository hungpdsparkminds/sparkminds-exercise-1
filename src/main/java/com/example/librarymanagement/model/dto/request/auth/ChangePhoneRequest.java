package com.example.librarymanagement.model.dto.request.auth;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePhoneRequest {
    @Pattern(regexp = "^[+](?:[\\d\\-\\(\\)\\/\\.]\\s?){6,15}[\\d]$")
    private String phone;
}