package com.netology.cloud.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Ответ при успешной авторизации.
 * Поле auth-token обязательно для совместимости с FRONT.
 */
@Getter
@AllArgsConstructor
public class LoginResponse {

    @JsonProperty("auth-token")
    private final String authToken;
}
