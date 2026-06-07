package com.netology.cloud.exception;

/**
 * Исключение при отсутствии или невалидности токена авторизации.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
