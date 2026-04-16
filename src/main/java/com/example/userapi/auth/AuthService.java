package com.example.userapi.auth;

import com.example.userapi.exception.InvalidCredentialsException;
import com.example.userapi.model.LoginRequest;
import com.example.userapi.repository.UserRepository;
import com.example.userapi.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthService(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    public String login(LoginRequest request) {
        if (request.getUsername() == null || request.getEmail() == null ||
            request.getUsername().isBlank() || request.getEmail().isBlank()) {
            throw new InvalidCredentialsException();
        }

        userRepository.findByUsernameIgnoreCaseAndEmailIgnoreCase(
                request.getUsername(), request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        return jwtUtil.generateToken(request.getUsername());
    }
}
