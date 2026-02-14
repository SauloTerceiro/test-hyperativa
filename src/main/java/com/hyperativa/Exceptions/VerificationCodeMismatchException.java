package com.hyperativa.Exceptions;

public class VerificationCodeMismatchException extends RuntimeException {
    public VerificationCodeMismatchException(String message) {
        super(message);
    }

}
