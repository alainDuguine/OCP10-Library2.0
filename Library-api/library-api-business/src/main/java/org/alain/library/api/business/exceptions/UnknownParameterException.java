package org.alain.library.api.business.exceptions;

public class UnknownParameterException extends RuntimeException {
    public UnknownParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
