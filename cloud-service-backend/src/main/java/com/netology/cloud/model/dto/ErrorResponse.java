package com.netology.cloud.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Стандартный ответ об ошибке по OpenAPI-спецификации.
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final String message;
    private final int id;
}
