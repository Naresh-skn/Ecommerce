package com.springboot.ecommerce.exception;

public class APIException extends RuntimeException{
    String message;
    public APIException(String message) {
        super(message);
        this.message=message;
    }
}
