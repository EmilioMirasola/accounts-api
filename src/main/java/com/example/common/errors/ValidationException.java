package com.example.common.errors;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
