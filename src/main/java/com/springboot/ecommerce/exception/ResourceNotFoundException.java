package com.springboot.ecommerce.exception;

public class ResourceNotFoundException extends RuntimeException{

    String field;
    String message;
    Long fieldId;

    public ResourceNotFoundException(String field, Long fieldId, String message) {
        super(field+" "+ fieldId +" "+message);
        this.field = field;
        this.message = message;
        this.fieldId = fieldId;
    }
}
