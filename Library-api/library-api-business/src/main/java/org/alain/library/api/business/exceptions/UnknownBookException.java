package org.alain.library.api.business.exceptions;

public class UnknownBookException extends RuntimeException {
    public UnknownBookException(String message) {
        super(message);
    }
}
