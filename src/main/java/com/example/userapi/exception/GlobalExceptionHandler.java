package com.example.userapi.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Locale;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    public GlobalExceptionHandler(MessageSource messageSource, LocaleResolver localeResolver) {
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        String message = messageSource.getMessage("error.user.not_found", new Object[]{ex.getUserId()}, locale);
        return build(HttpStatus.NOT_FOUND, message);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        String message = messageSource.getMessage("error.invalid_credentials", null, locale);
        return build(HttpStatus.UNAUTHORIZED, message);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        String message = messageSource.getMessage("error.route_not_found", new Object[]{ex.getRequestURL()}, locale);
        return build(HttpStatus.NOT_FOUND, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableMessage(HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        String message = messageSource.getMessage("error.malformed_json", null, locale);
        return build(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        String message = messageSource.getMessage("error.missing_parameter", new Object[]{ex.getParameterName()}, locale);
        return build(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ErrorResponse> handleServletException(ServletException ex, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        String message = messageSource.getMessage("error.method_not_allowed", new Object[]{ex.getMessage()}, locale);
        return build(HttpStatus.METHOD_NOT_ALLOWED, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        String message = messageSource.getMessage("error.generic", new Object[]{ex.getMessage()}, locale);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    private ResponseEntity<ErrorResponse> build(@NonNull HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), status.getReasonPhrase(), message));
    }
}
