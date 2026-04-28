package com.example.userapi.model;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String userName;

    public AuthResponse(String accessToken, String refreshToken, String userName) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userName = userName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getUserName() {
        return userName;
    }
}
