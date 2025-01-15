package com.axontic.ecommerce.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(resourceName + " not found with ID: " + resourceId);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
