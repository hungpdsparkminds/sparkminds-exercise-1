package com.example.librarymanagement.service;

import com.example.librarymanagement.model.dto.request.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine thymeleafTemplateEngine;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${application.base-url}")
    private String baseUrl;

    @Override
    public void sendVerificationEmail(String email, String token, String otp) throws MessagingException {
        Map<String, Object> attribute = new HashMap<>();
        attribute.put("baseUrl", baseUrl + "/api/v1/auth/verify-email");
        attribute.put("verificationToken", token);
        attribute.put("code", otp);
        sendMessageHtml(EmailRequest
                .builder()
                .to(email)
                .subject("SparkMinds1 - Please verify your email address")
                .template("email-verification")
                .attributes(attribute).build());
    }

    @Override
    public void sendForgotPassword(String email, String newPassword) throws MessagingException {
        Map<String, Object> attribute = new HashMap<>();
        attribute.put("password", newPassword);
        sendMessageHtml(EmailRequest
                .builder()
                .to(email)
                .subject("SparkMinds1 - Reset your password")
                .template("forgot-password")
                .attributes(attribute).build());
    }

    public void sendChangeEmailToken(String email, String token) throws MessagingException {
        Map<String, Object> attribute = new HashMap<>();
        attribute.put("token", token);
        sendMessageHtml(EmailRequest
                .builder()
                .to(email)
                .subject("SparkMinds1 - Change your email")
                .template("change-email-token")
                .attributes(attribute).build());
    }

    public void sendMessageHtml(EmailRequest emailRequest) throws MessagingException {
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(emailRequest.getAttributes());
        String htmlBody = thymeleafTemplateEngine.process(emailRequest.getTemplate(), thymeleafContext);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(username);
        helper.setTo(emailRequest.getTo());
        helper.setSubject(emailRequest.getSubject());
        helper.setText(htmlBody, true);
        mailSender.send(message);
    }
}
