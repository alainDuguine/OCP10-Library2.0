package org.alain.library.api.model.exceptions;

public class BookAlreadyLoanedException extends RuntimeException{
    public BookAlreadyLoanedException() {
    }

    public BookAlreadyLoanedException(String message) {
        super(message);
    }

    public BookAlreadyLoanedException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookAlreadyLoanedException(Throwable cause) {
        super(cause);
    }

    public BookAlreadyLoanedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
