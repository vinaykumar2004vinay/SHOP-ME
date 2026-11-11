package com.ecommerce.exception;

/**
 * ResourceNotFoundException — thrown when a requested entity is not found.
 * e.g., Product ID 999 doesn't exist → 404 response.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Convenience constructor to build message from resource type + id
    public ResourceNotFoundException(String resourceType, Long id) {
        super(resourceType + " not found with id: " + id);
    }
}
