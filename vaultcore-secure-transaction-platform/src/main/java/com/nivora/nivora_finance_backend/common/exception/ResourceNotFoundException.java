package com.nivora.nivora_finance_backend.common.exception;

public class ResourceNotFoundException  extends  RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
