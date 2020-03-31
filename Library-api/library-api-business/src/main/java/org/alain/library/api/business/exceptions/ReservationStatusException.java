package org.alain.library.api.business.exceptions;

public class ReservationStatusException extends RuntimeException {
    public ReservationStatusException() {
    }

    public ReservationStatusException(String message) {
        super(message);
    }

    public ReservationStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReservationStatusException(Throwable cause) {
        super(cause);
    }

    public ReservationStatusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
