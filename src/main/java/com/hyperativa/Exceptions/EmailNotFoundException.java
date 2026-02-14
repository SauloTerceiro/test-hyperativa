package com.hyperativa.Exceptions;

public class EmailNotFoundException extends RuntimeException{
    public EmailNotFoundException(String message) {
        super(message);
    }
}
