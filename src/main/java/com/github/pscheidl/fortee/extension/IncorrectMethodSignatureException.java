package com.github.pscheidl.fortee.extension;

/**
 * An exception thrown when a method signature does not meet criteria required.
 *
 * @author Pavel Pscheidl
 */
class IncorrectMethodSignatureException extends RuntimeException {

    /**
     * Constructs a new {@link IncorrectMethodSignatureException} with cause left uninitialized.
     *
     * @param message Message with exception cause
     */
    protected IncorrectMethodSignatureException(String message) {
        super(message);
    }

}
