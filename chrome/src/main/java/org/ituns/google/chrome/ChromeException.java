package org.ituns.google.chrome;

public class ChromeException extends Exception {

    public ChromeException(String message) {
        super(message);
    }

    public ChromeException(Throwable cause) {
        super(cause);
    }

    public ChromeException(String message, Throwable cause) {
        super(message, cause);
    }
}
