package com.netology.cloud.exception;

import com.netology.cloud.model.dto.LoginErrorResponse;
import lombok.Getter;

/**
 * Исключение при неверных учётных данных при входе.
 */
@Getter
public class LoginFailedException extends RuntimeException {

    private final LoginErrorResponse errorResponse;

    public LoginFailedException(LoginErrorResponse errorResponse) {
        super("Login failed");
        this.errorResponse = errorResponse;
    }
}
