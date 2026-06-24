package com.example.userapi.controller;

import com.example.userapi.dto.UserRequest;
import com.example.userapi.dto.UserResponse;
import com.example.userapi.exception.ApiResponse;
import com.example.userapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody UserRequest request) {
        UserResponse created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "User created successfully", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>(200, "Users fetched successfully", users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable @NonNull Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "User fetched successfully", user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable @NonNull Long id, @RequestBody UserRequest request) {
        UserResponse updated = userService.updateUser(id, request);
        return ResponseEntity.ok(new ApiResponse<>(200, "User updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable @NonNull Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "User deleted successfully", null));
    }
}
