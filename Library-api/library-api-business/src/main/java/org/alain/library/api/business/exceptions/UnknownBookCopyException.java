package org.alain.library.api.business.exceptions;

public class UnknownBookCopyException extends RuntimeException {

    public UnknownBookCopyException(String message) {
        super(message);
    }
}
