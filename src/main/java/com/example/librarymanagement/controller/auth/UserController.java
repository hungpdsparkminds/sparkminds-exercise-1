package com.example.librarymanagement.controller.auth;

import com.example.librarymanagement.model.dto.request.auth.ChangeEmailRequest;
import com.example.librarymanagement.model.dto.request.auth.ChangePasswordRequest;
import com.example.librarymanagement.service.auth.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Validated
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/2fa")
    public ResponseEntity<String> get2FAQr() {
        return ResponseEntity.ok(userService.getUser2FA());
    }

    @PatchMapping("/2fa")
    public ResponseEntity<Void> modifyUser2FA(@RequestParam("use2FA") boolean use2FA) {
        userService.updateUser2FA(use2FA);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(changePasswordRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-email")
    public ResponseEntity<Void> createChangeEmailRequest(
            @RequestBody @Valid ChangeEmailRequest changeEmailRequest) {
        userService.changeEmail(changeEmailRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/change-email")
    public ResponseEntity<Void> updateEmail(
            @RequestBody @Valid ChangeEmailRequest changeEmailRequest) {
        userService.updateEmail(changeEmailRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/change-phone")
    public ResponseEntity<Void> updatePhone(@RequestParam @Valid
                                                @Pattern(regexp = "^[+](?:[\\d\\-\\(\\)\\/\\.]\\s?){6,15}[\\d]$")
                                            String phone) {
        userService.updatePhone(phone);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/verify-phone")
    public ResponseEntity<Void> verifyPhone(@RequestParam String otp) {
        userService.verifyPhone(otp);
        return ResponseEntity.noContent().build();
    }
}
