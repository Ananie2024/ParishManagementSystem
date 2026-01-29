package org.ananie.parishManagementSystem.exception;

public class ResourceNotFoundException extends RuntimeException{
    private String message;

    public ResourceNotFoundException(String message) {
        // Create a meaningful message
        super(message);
        this.message = message;
    }
}
