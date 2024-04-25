package com.example.librarymanagement.controller.auth;

import com.example.librarymanagement.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Validated
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/update/2fa")
    public ResponseEntity<?> modifyUser2FA(@RequestParam("use2FA") boolean use2FA)
            throws IOException {
        userService.updateUser2FA(use2FA);
        return ResponseEntity.noContent().build();
    }
}
