package org.ananie.parishManagementSystem.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;

public class ValidationException extends RuntimeException {

    private String message;
    public  ValidationException(String message) {
        super(message);
        this.message = message;

    }
}
