package org.alain.library.api.model.exceptions;

public class FullReservationListException extends RuntimeException {
    public FullReservationListException() {
    }

    public FullReservationListException(String message) {
        super(message);
    }

    public FullReservationListException(String message, Throwable cause) {
        super(message, cause);
    }

    public FullReservationListException(Throwable cause) {
        super(cause);
    }

    public FullReservationListException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
