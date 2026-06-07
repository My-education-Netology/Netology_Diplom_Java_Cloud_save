package com.netology.cloud.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Настройки файлового хранилища из application.yml.
 */
@Component
@ConfigurationProperties(prefix = "storage.files")
@Getter
@Setter
public class StorageProperties {

    private String path = "./storage/files";
}
