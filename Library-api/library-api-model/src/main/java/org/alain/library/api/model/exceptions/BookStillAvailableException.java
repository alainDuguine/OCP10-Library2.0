package org.alain.library.api.model.exceptions;

public class BookStillAvailableException extends RuntimeException {
    public BookStillAvailableException() {
    }

    public BookStillAvailableException(String message) {
        super(message);
    }

    public BookStillAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookStillAvailableException(Throwable cause) {
        super(cause);
    }

    public BookStillAvailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
