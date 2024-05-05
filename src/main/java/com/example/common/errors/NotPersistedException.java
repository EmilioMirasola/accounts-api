package com.example.common.errors;

public class NotPersistedException extends RuntimeException {
    public NotPersistedException(String message) {
        super(message);
    }
}
