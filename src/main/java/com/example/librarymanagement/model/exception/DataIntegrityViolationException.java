package com.example.librarymanagement.model.exception;

import com.example.librarymanagement.utils.MessagesUtils;

public class DataIntegrityViolationException extends RuntimeException {

    private String message;

    public DataIntegrityViolationException(String errorCode, Object... var2) {
        this.message = MessagesUtils.getMessage(errorCode, var2);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}