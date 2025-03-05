package com.example.controller;

import com.example.dto.LoginRequest;
import com.example.entity.User;
import com.example.security.JwtTokenProvider;
import com.example.service.UserService;
import com.example.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    // Registration endpoint
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> registerUser(@Valid @RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User registered successfully!", "Success", HttpStatus.CREATED, registeredUser));
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            String token = tokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", "Success", HttpStatus.OK, token));
        } catch (BadCredentialsException ex) {
            // Differentiate errors as needed. For now, we rethrow with a clear message.
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}

