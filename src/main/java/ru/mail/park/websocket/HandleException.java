package ru.mail.park.websocket;

public class HandleException extends Exception {
    public HandleException() {
    }

    public HandleException(String message) {
        super(message);
    }

    public HandleException(String message, Throwable cause) {
        super(message, cause);
    }
}
