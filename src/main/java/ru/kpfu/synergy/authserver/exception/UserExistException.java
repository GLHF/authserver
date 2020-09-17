package ru.kpfu.synergy.authserver.exception;

public class UserExistException extends AuthException {
    public UserExistException(String message) {
        super(message);
    }
}
