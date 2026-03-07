package edu.cit.canadilla.wildcatslounge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Email or Student ID is required")
    private String identifier;

    @NotBlank(message = "Password is required")
    private String password;
}
