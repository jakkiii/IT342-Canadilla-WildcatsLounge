package edu.cit.canadilla.wildcatslounge.feature.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
    
    private final BCryptPasswordEncoder passwordEncoder;
    
    public PasswordUtil() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    /**
     * Hash a plain text password
     * @param plainPassword the plain text password
     * @return the hashed password
     */
    public String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }
    
    /**
     * Verify if a plain text password matches a hashed password
     * @param plainPassword the plain text password
     * @param hashedPassword the hashed password
     * @return true if passwords match, false otherwise
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}
