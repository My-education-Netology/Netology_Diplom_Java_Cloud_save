package com.netology.cloud.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Запрос на переименование файла.
 * FRONT отправляет поле filename, YAML-спецификация — name.
 */
@Getter
@Setter
public class RenameFileRequest {

    private String name;
    private String filename;

    /**
     * Возвращает новое имя файла из любого поддерживаемого поля.
     */
    public String resolveNewName() {
        if (filename != null && !filename.isBlank()) {
            return filename;
        }
        return name;
    }
}
