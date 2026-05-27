package edu.cit.canadilla.wildcatslounge.service;

import edu.cit.canadilla.wildcatslounge.entity.User;
import edu.cit.canadilla.wildcatslounge.repository.UserRepository;
import edu.cit.canadilla.wildcatslounge.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthHelperService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public User requireUser(HttpServletRequest request) {
        String token = extractBearerToken(request);
        if (token == null || !jwtUtil.isValid(token)) {
            throw new RuntimeException("Unauthorized");
        }
        String email = jwtUtil.extractEmail(token).orElseThrow(() -> new RuntimeException("Unauthorized"));
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Unauthorized"));
    }

    public User requireStaff(HttpServletRequest request) {
        User user = requireUser(request);
        if (!"staff".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Forbidden");
        }
        return user;
    }

    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
