package com.example.userapi.exception;

public class UserNotFoundException extends RuntimeException {

    private final Long userId;

    public UserNotFoundException(Long id) {
        super("User not found with id: " + id);
        this.userId = id;
    }

    public Long getUserId() {
        return userId;
    }
}
