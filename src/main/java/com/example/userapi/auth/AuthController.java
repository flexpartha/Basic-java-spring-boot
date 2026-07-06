package com.example.userapi.auth;

import com.example.userapi.exception.ApiResponse;
import com.example.userapi.exception.InvalidCredentialsException;
import com.example.userapi.model.AuthResponse;
import com.example.userapi.model.GoogleCodeRequest;
import com.example.userapi.model.LoginRequest;
import com.example.userapi.model.TokenResponse;
import com.example.userapi.repository.UserRepository;
import com.example.userapi.service.LoginNotificationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    @Autowired(required = false)
    private LoginNotificationService loginNotificationService;

    public AuthController(AuthService authService, UserRepository userRepository,
                          MessageSource messageSource, LocaleResolver localeResolver) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request,
            HttpServletRequest httpRequest, HttpServletResponse response) {
        AuthService.LoginResult result = authService.login(request);
        response.addCookie(buildRefreshCookie(result.refreshToken()));
        Locale locale = localeResolver.resolveLocale(httpRequest);
        return ResponseEntity.ok(new ApiResponse<>(200,
                messageSource.getMessage("success.login", null, locale), result.authResponse()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletRequest httpRequest) {
        Locale locale = localeResolver.resolveLocale(httpRequest);
        try {
            String accessToken = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(new ApiResponse<>(200,
                    messageSource.getMessage("success.refresh", null, locale),
                    new TokenResponse(accessToken)));
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(401).body(new ApiResponse<>(401,
                    messageSource.getMessage("error.refresh_invalid", null, locale), null));
        }
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(@RequestBody GoogleCodeRequest request,
            HttpServletRequest httpRequest, HttpServletResponse response) throws Exception {
        AuthService.LoginResult result = authService.googleLogin(request.getCode(), request.getCodeVerifier());
        response.addCookie(buildRefreshCookie(result.refreshToken()));
        Locale locale = localeResolver.resolveLocale(httpRequest);
        return ResponseEntity.ok(new ApiResponse<>(200,
                messageSource.getMessage("success.google_login", null, locale), result.authResponse()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest httpRequest, HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        Locale locale = localeResolver.resolveLocale(httpRequest);
        return ResponseEntity.ok(new ApiResponse<>(200,
                messageSource.getMessage("success.logout", null, locale), null));
    }

    private Cookie buildRefreshCookie(String value) {
        Cookie cookie = new Cookie("refreshToken", value);
        cookie.setHttpOnly(true);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge(2 * 60);
        return cookie;
    }
}
