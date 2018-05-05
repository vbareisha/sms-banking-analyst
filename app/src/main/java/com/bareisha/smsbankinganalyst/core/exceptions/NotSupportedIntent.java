package com.bareisha.smsbankinganalyst.core.exceptions;

public class NotSupportedIntent extends RuntimeException {
    public NotSupportedIntent() {
        super();
    }

    public NotSupportedIntent(String message) {
        super(message);
    }

    public NotSupportedIntent(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSupportedIntent(Throwable cause) {
        super(cause);
    }
}
