package com.example.userapi.auth;

import com.example.userapi.exception.ApiResponse;
import com.example.userapi.model.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@RequestBody LoginRequest request,
                                                                   jakarta.servlet.http.HttpServletRequest http) {
        String token = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Login successful", Map.of("token", token)));
    }
}
