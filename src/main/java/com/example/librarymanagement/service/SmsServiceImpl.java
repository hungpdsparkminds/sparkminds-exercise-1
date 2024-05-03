package com.example.librarymanagement.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {
    @Value("${twilio.account_sid}")
    private String accountSid;
    @Value("${twilio.auth_token}")
    private String authToken;
    @Value("${twilio.trial_number}")
    private String trialNumber;

    @Override
    public void sendSms(String phoneNumber, String message) {
        Twilio.init(accountSid, authToken);
        Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(trialNumber),
                message
        ).create();
    }

    @Override
    public void send6DigitCode(String phoneNumber, String code) {
        sendSms(phoneNumber, "%s is your SparkMinds1 verification code.".formatted(code));
    }
}
