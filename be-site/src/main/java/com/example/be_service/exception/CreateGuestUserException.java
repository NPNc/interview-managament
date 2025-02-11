package com.example.be_service.exception;

public class CreateGuestUserException extends  RuntimeException {
    public CreateGuestUserException(final String message) {
        super(message);
    }
}
