package com.example.librarymanagement.service.auth;

import com.example.librarymanagement.model.dto.request.auth.LoginRequest;
import com.example.librarymanagement.model.dto.response.auth.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse login(LoginRequest loginRequest);
}
