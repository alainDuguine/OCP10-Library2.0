package org.alain.library.api.business.exceptions;

public class UnknownAuthorException extends RuntimeException {

    public UnknownAuthorException() {
    }

    public UnknownAuthorException(String message) {
        super(message);
    }
}
