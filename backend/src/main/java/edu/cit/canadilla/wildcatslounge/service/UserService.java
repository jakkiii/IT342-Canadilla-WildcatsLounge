package edu.cit.canadilla.wildcatslounge.service;

import edu.cit.canadilla.wildcatslounge.dto.AuthResponse;
import edu.cit.canadilla.wildcatslounge.dto.LoginRequest;
import edu.cit.canadilla.wildcatslounge.dto.RegisterRequest;
import edu.cit.canadilla.wildcatslounge.entity.User;
import edu.cit.canadilla.wildcatslounge.repository.UserRepository;
import edu.cit.canadilla.wildcatslounge.util.JwtUtil;
import edu.cit.canadilla.wildcatslounge.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;

    /**
     * Register a new student user.
     */
    @Transactional
    public AuthResponse registerUser(RegisterRequest request) {
        String email = normalizeEmail(request.getEmail());
        validateRegistration(request, email);

        // Build user entity
        User user = new User();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(email);
        user.setStudentId(request.getStudentId().trim());
        user.setPassword(passwordUtil.hashPassword(request.getPassword()));
        user.setRole("student");

        User savedUser = userRepository.save(user);

        return buildAuthResponse(savedUser);
    }

    /**
     * Login user — identifier can be an email address or a student_id.
     * If the identifier contains "@" it is treated as an email; otherwise as a student_id.
     */
    public AuthResponse loginUser(LoginRequest request) {
        return buildAuthResponse(authenticateUser(request));
    }

    public void assertCanRegister(RegisterRequest request) {
        validateRegistration(request, normalizeEmail(request.getEmail()));
    }

    public User authenticateUser(LoginRequest request) {
        String identifier = request.getIdentifier().trim();

        Optional<User> userOptional = identifier.contains("@")
                ? userRepository.findByEmailIgnoreCase(normalizeEmail(identifier))
                : userRepository.findByStudentId(identifier);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }

        User user = userOptional.get();

        // Verify BCrypt password
        if (!passwordUtil.verifyPassword(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }

    public AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        AuthResponse.UserData userData = new AuthResponse.UserData(
                user.getId(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                user.getStudentId(),
                user.getRole()
        );

        return new AuthResponse(userData, accessToken, refreshToken);
    }

    private void validateRegistration(RegisterRequest request, String email) {
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        String studentId = request.getStudentId() == null ? "" : request.getStudentId().trim();
        if (studentId.isBlank()) {
            throw new RuntimeException("Student ID is required");
        }

        if (userRepository.existsByStudentId(studentId)) {
            throw new RuntimeException("Student ID already registered");
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ENGLISH);
    }
}
