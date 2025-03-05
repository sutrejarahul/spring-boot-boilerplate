package com.example.util;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        boolean success,
        String message,
        String type,
        String description,
        int status,
        T data
) {
    public ApiResponse(boolean success, String message, String type, String description, HttpStatus status, T data) {
        this(success, message, type, description, status.value(), data);
    }

    // Constructor Overload for Default Type "RESPONSE"
    public ApiResponse(boolean success, String message, String description, HttpStatus status, T data) {
        this(success, message, "RESPONSE", description, status, data);
    }
}

