package com.netology.cloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Формат ошибок авторизации, ожидаемый FRONT.
 */
@Getter
@Setter
public class LoginErrorResponse {

    private List<String> email = new ArrayList<>();
    private List<String> password = new ArrayList<>();
}
