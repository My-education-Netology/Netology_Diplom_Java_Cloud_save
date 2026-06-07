package com.netology.cloud.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Элемент списка файлов для FRONT.
 */
@Getter
@AllArgsConstructor
public class FileInfoResponse {

    private final String filename;
    private final long size;
    private final long editedAt;
}
