package com.example.userapi.auth;

import com.example.userapi.exception.InvalidCredentialsException;
import com.example.userapi.model.AuthResponse;
import com.example.userapi.model.LoginRequest;
import com.example.userapi.model.User;
import com.example.userapi.repository.UserRepository;
import com.example.userapi.security.JwtUtil;
import com.example.userapi.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Autowired(required = false)
    private EmailService emailService;

    public AuthService(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    public LoginResult login(LoginRequest request) {
        if (request.getUsername() == null || request.getEmail() == null ||
                request.getUsername().isBlank() || request.getEmail().isBlank()) {
            throw new InvalidCredentialsException();
        }

        User user = userRepository.findByUsernameIgnoreCaseAndEmailIgnoreCase(
                request.getUsername(), request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (emailService != null) {
            emailService.sendLoginNotification(user.getName(), user.getUsername(), user.getEmail());
        }

        String accessToken = jwtUtil.generateAccessToken(request.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(request.getUsername());
        return new LoginResult(new AuthResponse(accessToken, user.getUsername()), refreshToken);
    }

    public record LoginResult(AuthResponse authResponse, String refreshToken) {}

    public String refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank() || !jwtUtil.validateToken(refreshToken)) {
            throw new InvalidCredentialsException();
        }
        return jwtUtil.generateAccessToken(jwtUtil.extractUsername(refreshToken));
    }
}
