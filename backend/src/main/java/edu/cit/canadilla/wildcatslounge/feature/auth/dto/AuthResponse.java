package edu.cit.canadilla.wildcatslounge.feature.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private UserData user;
    private String accessToken;
    private String refreshToken;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserData {
        private Long id;
        private String email;
        private String firstname;
        private String lastname;
        private String studentId;
        private String role;
    }
}
