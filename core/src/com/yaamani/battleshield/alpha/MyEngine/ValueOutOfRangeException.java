package com.yaamani.battleshield.alpha.MyEngine;

public class ValueOutOfRangeException extends RuntimeException {

    public ValueOutOfRangeException() {

    }

    public ValueOutOfRangeException(String message) {
        super(message);
    }

    public ValueOutOfRangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValueOutOfRangeException(Throwable cause) {
        super(cause);
    }
}
