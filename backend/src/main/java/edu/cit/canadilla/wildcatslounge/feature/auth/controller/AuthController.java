package edu.cit.canadilla.wildcatslounge.feature.auth.controller;

import edu.cit.canadilla.wildcatslounge.common.ApiResponse;
import edu.cit.canadilla.wildcatslounge.feature.auth.dto.AuthResponse;
import edu.cit.canadilla.wildcatslounge.feature.auth.dto.LoginRequest;
import edu.cit.canadilla.wildcatslounge.feature.auth.dto.RegisterRequest;
import edu.cit.canadilla.wildcatslounge.feature.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow frontend to access API
public class AuthController {
    
    private final UserService userService;
    
    /**
     * Health check endpoint
     * @return API status message
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse> health() {
        return ResponseEntity.ok(
            ApiResponse.success("Wildcats Lounge API is running")
        );
    }
    
    /**
     * Register a new user
     * @param request Registration request
     * @param bindingResult Validation result
     * @return Response with user data or error message
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(
            @Valid @RequestBody RegisterRequest request,
            BindingResult bindingResult) {
        
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(errors));
        }
        
        try {
            AuthResponse authResponse = userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(authResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Login user
     * @param request Login request
     * @param bindingResult Validation result
     * @return Response with user data or error message
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(
            @Valid @RequestBody LoginRequest request,
            BindingResult bindingResult) {
        
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(errors));
        }
        
        try {
            AuthResponse authResponse = userService.loginUser(request);
            return ResponseEntity.ok(ApiResponse.success(authResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
