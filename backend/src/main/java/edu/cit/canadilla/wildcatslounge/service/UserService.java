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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;

    /**
     * Register a new user.
     * Assigns role "student" if a student_id is provided, otherwise "staff".
     */
    @Transactional
    public AuthResponse registerUser(RegisterRequest request) {
        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new RuntimeException("Email already registered");
        }

        // Check for duplicate student_id (only if provided)
        boolean hasStudentId = request.getStudentId() != null && !request.getStudentId().isBlank();
        if (hasStudentId && userRepository.existsByStudentId(request.getStudentId())) {
            throw new RuntimeException("Student ID already registered");
        }

        // Build user entity
        User user = new User();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail().toLowerCase());
        user.setStudentId(hasStudentId ? request.getStudentId() : null);
        user.setPassword(passwordUtil.hashPassword(request.getPassword()));
        user.setRole(hasStudentId ? "student" : "staff");

        User savedUser = userRepository.save(user);

        // Generate JWT tokens
        String accessToken = jwtUtil.generateAccessToken(savedUser.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getEmail());

        AuthResponse.UserData userData = new AuthResponse.UserData(
            savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstname(),
                savedUser.getLastname(),
                savedUser.getStudentId(),
                savedUser.getRole()
        );

        return new AuthResponse(userData, accessToken, refreshToken);
    }

    /**
     * Login user — identifier can be an email address or a student_id.
     * If the identifier contains "@" it is treated as an email; otherwise as a student_id.
     */
    public AuthResponse loginUser(LoginRequest request) {
        String identifier = request.getIdentifier().trim();

        Optional<User> userOptional = identifier.contains("@")
                ? userRepository.findByEmail(identifier.toLowerCase())
                : userRepository.findByStudentId(identifier);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }

        User user = userOptional.get();

        // Verify BCrypt password
        if (!passwordUtil.verifyPassword(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate JWT tokens
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
}
