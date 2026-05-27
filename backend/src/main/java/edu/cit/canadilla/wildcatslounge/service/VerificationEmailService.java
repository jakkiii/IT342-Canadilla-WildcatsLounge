package edu.cit.canadilla.wildcatslounge.service;

import edu.cit.canadilla.wildcatslounge.entity.EmailVerificationCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationEmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:}")
    private String mailFrom;

    public void sendVerificationCode(
            String recipientEmail,
            String verificationCode,
            EmailVerificationCode.Purpose purpose,
            long expiryMinutes
    ) {
        if (mailFrom == null || mailFrom.isBlank()) {
            throw new RuntimeException("Email sender is not configured. Set MAIL_USERNAME and MAIL_PASSWORD in backend/.env.");
        }

        String action = purpose == EmailVerificationCode.Purpose.REGISTER ? "registration" : "login";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(recipientEmail);
        message.setSubject("Wildcats Lounge " + capitalize(action) + " verification code");
        message.setText("""
                Hello,

                Your Wildcats Lounge %s verification code is:

                %s

                This code will expire in %d minutes.

                If you did not request this code, you can ignore this email.

                Wildcats Lounge
                """.formatted(action, verificationCode, expiryMinutes));

        try {
            mailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException(
                    "Could not send verification code. Check the Gmail sender settings and app password in backend/.env."
            );
        }
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}
