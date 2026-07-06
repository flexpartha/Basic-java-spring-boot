package com.example.userapi.controller;

import com.example.userapi.dto.UserRequest;
import com.example.userapi.dto.UserResponse;
import com.example.userapi.exception.ApiResponse;
import com.example.userapi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    public UserController(UserService userService, MessageSource messageSource, LocaleResolver localeResolver) {
        this.userService = userService;
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody UserRequest request,
            HttpServletRequest httpRequest) {
        UserResponse created = userService.createUser(request);
        Locale locale = localeResolver.resolveLocale(httpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201,
                        messageSource.getMessage("success.user.created", null, locale), created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(HttpServletRequest httpRequest) {
        List<UserResponse> users = userService.getAllUsers();
        Locale locale = localeResolver.resolveLocale(httpRequest);
        return ResponseEntity.ok(new ApiResponse<>(200,
                messageSource.getMessage("success.user.fetched_all", null, locale), users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable @NonNull Long id,
            HttpServletRequest httpRequest) {
        UserResponse user = userService.getUserById(id);
        Locale locale = localeResolver.resolveLocale(httpRequest);
        return ResponseEntity.ok(new ApiResponse<>(200,
                messageSource.getMessage("success.user.fetched", null, locale), user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable @NonNull Long id,
            @RequestBody UserRequest request, HttpServletRequest httpRequest) {
        UserResponse updated = userService.updateUser(id, request);
        Locale locale = localeResolver.resolveLocale(httpRequest);
        return ResponseEntity.ok(new ApiResponse<>(200,
                messageSource.getMessage("success.user.updated", null, locale), updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable @NonNull Long id,
            HttpServletRequest httpRequest) {
        userService.deleteUser(id);
        Locale locale = localeResolver.resolveLocale(httpRequest);
        return ResponseEntity.ok(new ApiResponse<>(200,
                messageSource.getMessage("success.user.deleted", null, locale), null));
    }
}
