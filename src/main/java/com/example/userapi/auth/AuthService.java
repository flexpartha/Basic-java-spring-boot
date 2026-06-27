package com.example.userapi.auth;

import com.example.userapi.exception.InvalidCredentialsException;
import com.example.userapi.model.AuthResponse;
import com.example.userapi.model.LoginRequest;
import com.example.userapi.model.User;
import com.example.userapi.repository.UserRepository;
import com.example.userapi.security.JwtUtil;
import com.example.userapi.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;

@Service
public class AuthService {

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${google.client-secret}")
    private String googleClientSecret;

    @Value("${google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${google.token-uri}")
    private String googleTokenUri;

    private final RestTemplate restTemplate = new RestTemplate();

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

    @SuppressWarnings("unchecked")
    public LoginResult googleLogin(String code, String codeVerifier) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        if (codeVerifier == null || codeVerifier.isBlank()) {
              throw new IllegalArgumentException("codeVerifier is required for PKCE");
          }
        params.add("code_verifier", codeVerifier);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
                googleTokenUri, new HttpEntity<>(params, headers), Map.class);

        if (!tokenResponse.getStatusCode().is2xxSuccessful() || tokenResponse.getBody() == null
                || !tokenResponse.getBody().containsKey("id_token")) {
            throw new InvalidCredentialsException();
        }

        // Decode the id_token payload (JWT, no verification needed — Google already validated it)
        String idToken = (String) tokenResponse.getBody().get("id_token");
        String payload = new String(java.util.Base64.getUrlDecoder()
                .decode(idToken.split("\\.")[1]));
        Map<String, Object> claims = new com.fasterxml.jackson.databind.ObjectMapper()
                .readValue(payload, Map.class);
        String email = (String) claims.get("email");
        String name  = (String) claims.getOrDefault("name", email);

        String accessToken  = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);
        return new LoginResult(new AuthResponse(accessToken, name), refreshToken);
    }

    public String refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank() || !jwtUtil.validateToken(refreshToken)) {
            throw new InvalidCredentialsException();
        }
        return jwtUtil.generateAccessToken(jwtUtil.extractUsername(refreshToken));
    }
}
