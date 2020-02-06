package org.alain.library.api.business.exceptions;

public class UnknownUserException extends RuntimeException {
    public UnknownUserException(String message) {
        super(message);
    }
}
