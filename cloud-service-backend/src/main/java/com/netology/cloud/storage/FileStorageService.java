package com.netology.cloud.storage;

import com.netology.cloud.config.StorageProperties;
import com.netology.cloud.exception.FileOperationException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Сервис хранения бинарных файлов на диске.
 */
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final StorageProperties storageProperties;

    /**
     * Сохраняет загруженный файл в каталог пользователя.
     */
    public Path store(Long userId, String filename, MultipartFile file) {
        try {
            Path userDir = resolveUserDirectory(userId);
            Files.createDirectories(userDir);
            Path target = userDir.resolve(filename).normalize();

            if (!target.startsWith(userDir)) {
                throw new FileOperationException("Недопустимое имя файла", HttpStatus.BAD_REQUEST.value());
            }

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return target;
        } catch (IOException ex) {
            throw new FileOperationException("Ошибка загрузки файла", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /**
     * Загружает файл как Resource для скачивания.
     */
    public Resource loadAsResource(Path storagePath) {
        try {
            Resource resource = new UrlResource(storagePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new FileOperationException("Файл не найден", HttpStatus.BAD_REQUEST.value());
            }
            return resource;
        } catch (IOException ex) {
            throw new FileOperationException("Ошибка скачивания файла", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /**
     * Переименовывает файл на диске.
     */
    public Path rename(Path currentPath, String newFilename) {
        try {
            Path newPath = currentPath.getParent().resolve(newFilename).normalize();
            Files.move(currentPath, newPath, StandardCopyOption.REPLACE_EXISTING);
            return newPath;
        } catch (IOException ex) {
            throw new FileOperationException("Ошибка переименования файла", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /**
     * Удаляет файл с диска.
     */
    public void delete(Path storagePath) {
        try {
            Files.deleteIfExists(storagePath);
        } catch (IOException ex) {
            throw new FileOperationException("Ошибка удаления файла", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private Path resolveUserDirectory(Long userId) {
        return Path.of(storageProperties.getPath(), String.valueOf(userId)).toAbsolutePath().normalize();
    }
}
