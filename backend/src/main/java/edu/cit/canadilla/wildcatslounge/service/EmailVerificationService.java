package edu.cit.canadilla.wildcatslounge.service;

import edu.cit.canadilla.wildcatslounge.entity.EmailVerificationCode;
import edu.cit.canadilla.wildcatslounge.repository.EmailVerificationCodeRepository;
import edu.cit.canadilla.wildcatslounge.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final EmailVerificationCodeRepository emailVerificationCodeRepository;
    private final PasswordUtil passwordUtil;
    private final VerificationEmailService verificationEmailService;

    @Value("${app.auth.otp.expiry-minutes:10}")
    private long otpExpiryMinutes;

    @Value("${app.auth.otp.resend-cooldown-seconds:60}")
    private long resendCooldownSeconds;

    @Transactional
    public void sendRegisterCode(String email) {
        sendCode(email, EmailVerificationCode.Purpose.REGISTER);
    }

    @Transactional
    public void sendLoginCode(String email) {
        sendCode(email, EmailVerificationCode.Purpose.LOGIN);
    }

    @Transactional
    public void verifyRegisterCode(String email, String code) {
        verifyCode(email, EmailVerificationCode.Purpose.REGISTER, code);
    }

    @Transactional
    public void verifyLoginCode(String email, String code) {
        verifyCode(email, EmailVerificationCode.Purpose.LOGIN, code);
    }

    private void sendCode(String rawEmail, EmailVerificationCode.Purpose purpose) {
        String email = normalizeEmail(rawEmail);
        assertResendAllowed(email, purpose);
        invalidateExistingCodes(email, purpose);

        String verificationCode = generateVerificationCode();
        EmailVerificationCode record = new EmailVerificationCode();
        record.setEmail(email);
        record.setPurpose(purpose);
        record.setCodeHash(passwordUtil.hashPassword(verificationCode));
        record.setExpiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
        emailVerificationCodeRepository.save(record);

        verificationEmailService.sendVerificationCode(email, verificationCode, purpose, otpExpiryMinutes);
    }

    private void verifyCode(String rawEmail, EmailVerificationCode.Purpose purpose, String code) {
        String email = normalizeEmail(rawEmail);
        EmailVerificationCode latest = emailVerificationCodeRepository
                .findTopByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(email, purpose)
                .orElseThrow(() -> new RuntimeException("Send a verification code first."));

        if (latest.getConsumedAt() != null) {
            throw new RuntimeException("This verification code was already used. Request a new one.");
        }

        if (latest.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification code expired. Request a new one.");
        }

        if (!passwordUtil.verifyPassword(code, latest.getCodeHash())) {
            throw new RuntimeException("Invalid verification code.");
        }

        latest.setConsumedAt(LocalDateTime.now());
        emailVerificationCodeRepository.save(latest);
    }

    private void assertResendAllowed(String email, EmailVerificationCode.Purpose purpose) {
        emailVerificationCodeRepository.findTopByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(email, purpose)
                .ifPresent(record -> {
                    LocalDateTime nextAllowedAt = record.getCreatedAt().plusSeconds(resendCooldownSeconds);
                    if (record.getConsumedAt() == null && nextAllowedAt.isAfter(LocalDateTime.now())) {
                        long secondsLeft = java.time.Duration.between(LocalDateTime.now(), nextAllowedAt).getSeconds();
                        throw new RuntimeException("Please wait " + Math.max(secondsLeft, 1) + " seconds before requesting another code.");
                    }
                });
    }

    private void invalidateExistingCodes(String email, EmailVerificationCode.Purpose purpose) {
        List<EmailVerificationCode> activeCodes = emailVerificationCodeRepository
                .findByEmailIgnoreCaseAndPurposeAndConsumedAtIsNull(email, purpose);

        LocalDateTime now = LocalDateTime.now();
        activeCodes.forEach(code -> code.setConsumedAt(now));
        emailVerificationCodeRepository.saveAll(activeCodes);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ENGLISH);
    }

    private String generateVerificationCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }
}
