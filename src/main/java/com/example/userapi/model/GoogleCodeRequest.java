package com.example.userapi.model;

import lombok.Data;

@Data
public class GoogleCodeRequest {
    private String code;
    private String codeVerifier;
}
