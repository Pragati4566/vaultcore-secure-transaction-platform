package com.nivora.nivora_finance_backend.common.exception;

public class InvalidOtpException extends  RuntimeException {
    public InvalidOtpException(String message) {
        super(message);
    }
}
