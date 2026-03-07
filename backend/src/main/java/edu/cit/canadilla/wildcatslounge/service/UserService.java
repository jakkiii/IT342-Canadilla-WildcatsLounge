package edu.cit.canadilla.wildcatslounge.service;

import edu.cit.canadilla.wildcatslounge.dto.LoginRequest;
import edu.cit.canadilla.wildcatslounge.dto.RegisterRequest;
import edu.cit.canadilla.wildcatslounge.dto.UserResponse;
import edu.cit.canadilla.wildcatslounge.entity.User;
import edu.cit.canadilla.wildcatslounge.repository.UserRepository;
import edu.cit.canadilla.wildcatslounge.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    
    /**
     * Register a new user
     * @param request Registration request containing user details
     * @return UserResponse with created user information
     * @throws RuntimeException if email already exists
     */
    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        // Create new user entity
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail().toLowerCase()); // Normalize email to lowercase
        user.setPassword(passwordUtil.hashPassword(request.getPassword()));
        
        // Save user to database
        User savedUser = userRepository.save(user);
        
        // Convert to response DTO
        return convertToUserResponse(savedUser);
    }
    
    /**
     * Login user with credentials
     * @param request Login request containing email and password
     * @return UserResponse if login successful
     * @throws RuntimeException if credentials are invalid
     */
    public UserResponse loginUser(LoginRequest request) {
        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail().toLowerCase());
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }
        
        User user = userOptional.get();
        
        // Verify password
        if (!passwordUtil.verifyPassword(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        // Return user information
        return convertToUserResponse(user);
    }
    
    /**
     * Convert User entity to UserResponse DTO
     * @param user User entity
     * @return UserResponse DTO
     */
    private UserResponse convertToUserResponse(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createdAt = user.getCreatedAt().format(formatter);
        
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            createdAt
        );
    }
}
