package com.example.be_service.exception;

import com.example.be_service.utils.MessagesUtils;
import lombok.Setter;

@Setter
public class NotFoundException extends RuntimeException {
    private String message;

    public NotFoundException(String errorCode, Object... var2) {
        this.message = MessagesUtils.getMessage(errorCode, var2);
    }

    @Override
    public String getMessage() {
        return message;
    }

}
