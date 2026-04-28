package com.example.userapi.auth;

import com.example.userapi.exception.InvalidCredentialsException;
import com.example.userapi.model.AuthResponse;
import com.example.userapi.model.LoginRequest;
import com.example.userapi.model.RefreshRequest;
import com.example.userapi.model.User;
import com.example.userapi.repository.UserRepository;
import com.example.userapi.security.JwtUtil;
import com.example.userapi.service.EmailService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public AuthService(JwtUtil jwtUtil, UserRepository userRepository, EmailService emailService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public AuthResponse login(LoginRequest request) {
        if (request.getUsername() == null || request.getEmail() == null ||
                request.getUsername().isBlank() || request.getEmail().isBlank()) {
            throw new InvalidCredentialsException();
        }

        User user = userRepository.findByUsernameIgnoreCaseAndEmailIgnoreCase(
                request.getUsername(), request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        emailService.sendLoginNotification(user.getName(), user.getUsername(), user.getEmail());

        String accessToken = jwtUtil.generateAccessToken(request.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(request.getUsername());
        return new AuthResponse(accessToken, refreshToken, user.getUsername());
    }

    public String refreshToken(RefreshRequest request) {
        if (request == null || request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            throw new InvalidCredentialsException();
        }

        String refreshToken = request.getRefreshToken();
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new InvalidCredentialsException();
        }

        String username = jwtUtil.extractUsername(refreshToken);
        return jwtUtil.generateAccessToken(username);
    }
}
