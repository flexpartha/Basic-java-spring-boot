package com.example.userapi.auth;

import com.example.userapi.exception.ApiResponse;
import com.example.userapi.model.AuthResponse;
import com.example.userapi.model.GoogleCodeRequest;
import com.example.userapi.model.LoginRequest;
import com.example.userapi.model.TokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired(required = false)
    private LoginNotificationService loginNotificationService;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request,
            HttpServletResponse response) {
        AuthService.LoginResult result = authService.login(request);
        response.addCookie(buildRefreshCookie(result.refreshToken()));
        return ResponseEntity.ok(new ApiResponse<>(200, "Login successful", result.authResponse()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        try {
            String accessToken = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(new ApiResponse<>(200, "Refresh successful", new TokenResponse(accessToken)));
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(401).body(new ApiResponse<>(401, "Refresh token invalid or expired", null));
        }
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(@RequestBody GoogleCodeRequest request,
            HttpServletResponse response) throws Exception {
        AuthService.LoginResult result = authService.googleLogin(request.getCode(), request.getCodeVerifier());
        response.addCookie(buildRefreshCookie(result.refreshToken()));
        return ResponseEntity.ok(new ApiResponse<>(200, "Google login successful", result.authResponse()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok(new ApiResponse<>(200, "Logout successful", null));
    }

    private Cookie buildRefreshCookie(String value) {
        Cookie cookie = new Cookie("refreshToken", value);
        cookie.setHttpOnly(true);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge(2 * 60); // 2 minutes
        return cookie;
    }
}
