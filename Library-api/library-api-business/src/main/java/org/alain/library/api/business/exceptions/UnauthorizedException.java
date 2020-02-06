package org.alain.library.api.business.exceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
