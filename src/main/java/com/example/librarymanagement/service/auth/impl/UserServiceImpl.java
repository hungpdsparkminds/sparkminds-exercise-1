package com.example.librarymanagement.service.auth.impl;

import com.example.librarymanagement.model.entity.auth.User;
import com.example.librarymanagement.repository.auth.UserRepository;
import com.example.librarymanagement.service.auth.UserService;
import com.example.librarymanagement.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.glxn.qrgen.QRCode;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthUtils authUtils;
    @Value("${application.name}")
    private String applicationName;


    @Override
    public User updateUser2FA(boolean use2FA) throws IOException {
        var authenticatedUser = authUtils.getAuthenticatedUser();
        if (authenticatedUser.isUsing2FA() == use2FA) {
            return null;
        }
        authenticatedUser.setUsing2FA(use2FA);
        if (use2FA) {
            authenticatedUser.setSecret(Base32.random());
            var content = this.generateQRUrl(authenticatedUser, applicationName);
            ByteArrayOutputStream qrCodeStream = QRCode.from(content)
                    .withSize(200, 200)
                    .stream();
            Files.write(Path.of("totp/" + authenticatedUser.getUserId() + "_"
                            + authenticatedUser.getEmail() + ".png"),
                    qrCodeStream.toByteArray());
        }
        return userRepository.save(authenticatedUser);
    }
}
