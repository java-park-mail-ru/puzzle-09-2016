package ru.mail.park.model.exception;

public class UserAlreadyExistsException extends DaoException {
    private static final String MESSAGE = "User already exists";

    public UserAlreadyExistsException() {
        super(MESSAGE);
    }

    public UserAlreadyExistsException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
