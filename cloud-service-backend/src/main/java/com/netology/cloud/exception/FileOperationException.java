package com.netology.cloud.exception;

import lombok.Getter;

/**
 * Исключение при операциях с файлами (загрузка, удаление, переименование).
 */
@Getter
public class FileOperationException extends RuntimeException {

    private final int statusCode;

    public FileOperationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
