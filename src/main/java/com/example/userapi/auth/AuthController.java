package com.example.userapi.auth;

import com.example.userapi.exception.ApiResponse;
import com.example.userapi.model.AuthResponse;
import com.example.userapi.model.LoginRequest;
import com.example.userapi.model.RefreshRequest;
import com.example.userapi.model.TokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.userapi.exception.InvalidCredentialsException;
import com.example.userapi.repository.UserRepository;
import com.example.userapi.service.LoginNotificationService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final LoginNotificationService loginNotificationService;

    public AuthController(AuthService authService, UserRepository userRepository,
            LoginNotificationService loginNotificationService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.loginNotificationService = loginNotificationService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Login successful", authResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@RequestBody RefreshRequest request) {
        try {
            String accessToken = authService.refreshToken(request);
            return ResponseEntity.ok(new ApiResponse<>(200, "Refresh successful", new TokenResponse(accessToken)));
        } catch (InvalidCredentialsException e) {
            // Refresh token is invalid or expired
            return ResponseEntity.status(401).body(new ApiResponse<>(401, "Refresh token invalid or expired", null));
        }
    }
}
