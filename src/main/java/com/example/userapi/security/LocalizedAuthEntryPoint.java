package com.example.userapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class LocalizedAuthEntryPoint implements AuthenticationEntryPoint {

    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LocalizedAuthEntryPoint(MessageSource messageSource, LocaleResolver localeResolver) {
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        Locale locale = localeResolver.resolveLocale(request);
        String message = messageSource.getMessage("error.invalid_credentials", null, locale);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 401);
        body.put("error", "Unauthorized");
        body.put("message", message);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
