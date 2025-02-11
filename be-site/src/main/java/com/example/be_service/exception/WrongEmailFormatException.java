package com.example.be_service.exception;

import com.example.be_service.utils.MessagesUtils;
import lombok.Setter;

@Setter
public class WrongEmailFormatException extends RuntimeException {

    private String message;

    public WrongEmailFormatException(String errorCode, Object... var2) {
        this.message = MessagesUtils.getMessage(errorCode, var2);
    }

    @Override
    public String getMessage() {
        return message;
    }

}
