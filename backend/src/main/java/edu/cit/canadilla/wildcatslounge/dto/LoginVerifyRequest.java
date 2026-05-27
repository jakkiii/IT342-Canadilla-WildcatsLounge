package edu.cit.canadilla.wildcatslounge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVerifyRequest {

    @NotBlank(message = "Email or Student ID is required")
    private String identifier;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Verification code is required")
    @Pattern(regexp = "^\\d{6}$", message = "Verification code must be 6 digits")
    private String verificationCode;

    public LoginRequest toLoginRequest() {
        return new LoginRequest(identifier, password);
    }
}
