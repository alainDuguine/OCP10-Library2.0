package org.alain.library.api.business.exceptions;

public class UnknownLoanException extends RuntimeException {
    public UnknownLoanException(String message) {
        super(message);
    }
}
