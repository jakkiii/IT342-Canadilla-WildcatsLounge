package edu.cit.canadilla.wildcatslounge.repository;

import edu.cit.canadilla.wildcatslounge.entity.EmailVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long> {

    Optional<EmailVerificationCode> findTopByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(
            String email,
            EmailVerificationCode.Purpose purpose
    );

    List<EmailVerificationCode> findByEmailIgnoreCaseAndPurposeAndConsumedAtIsNull(
            String email,
            EmailVerificationCode.Purpose purpose
    );
}
