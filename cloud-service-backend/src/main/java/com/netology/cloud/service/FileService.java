package com.netology.cloud.service;

import com.netology.cloud.exception.FileOperationException;
import com.netology.cloud.model.dto.FileInfoResponse;
import com.netology.cloud.model.dto.RenameFileRequest;
import com.netology.cloud.model.entity.StoredFile;
import com.netology.cloud.model.entity.User;
import com.netology.cloud.repository.StoredFileRepository;
import com.netology.cloud.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

/**
 * Сервис операций с файлами пользователя.
 */
@Service
@RequiredArgsConstructor
public class FileService {

    private final StoredFileRepository storedFileRepository;
    private final FileStorageService fileStorageService;

    /**
     * Возвращает список файлов пользователя с учётом лимита.
     */
    @Transactional(readOnly = true)
    public List<FileInfoResponse> listFiles(User user, Integer limit) {
        int pageSize = limit != null && limit > 0 ? limit : Integer.MAX_VALUE;
        return storedFileRepository.findByUserOrderByEditedAtDesc(user, PageRequest.of(0, pageSize))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Загружает файл пользователя.
     */
    @Transactional
    public void uploadFile(User user, String filename, MultipartFile file) {
        validateFilename(filename);
        if (file == null || file.isEmpty()) {
            throw new FileOperationException("Файл не передан", HttpStatus.BAD_REQUEST.value());
        }
        if (storedFileRepository.existsByUserAndFilename(user, filename)) {
            throw new FileOperationException("Файл с таким именем уже существует", HttpStatus.BAD_REQUEST.value());
        }

        Path storagePath = fileStorageService.store(user.getId(), filename, file);

        StoredFile storedFile = new StoredFile();
        storedFile.setUser(user);
        storedFile.setFilename(filename);
        storedFile.setSize(file.getSize());
        storedFile.setStoragePath(storagePath.toString());
        storedFileRepository.save(storedFile);
    }

    /**
     * Скачивает файл пользователя.
     */
    @Transactional(readOnly = true)
    public Resource downloadFile(User user, String filename) {
        validateFilename(filename);
        StoredFile storedFile = findUserFile(user, filename);
        return fileStorageService.loadAsResource(Path.of(storedFile.getStoragePath()));
    }

    /**
     * Переименовывает файл пользователя.
     */
    @Transactional
    public void renameFile(User user, String oldFilename, RenameFileRequest request) {
        validateFilename(oldFilename);
        String newFilename = request.resolveNewName();
        if (newFilename == null || newFilename.isBlank()) {
            throw new FileOperationException("Новое имя файла не указано", HttpStatus.BAD_REQUEST.value());
        }
        if (storedFileRepository.existsByUserAndFilename(user, newFilename)) {
            throw new FileOperationException("Файл с таким именем уже существует", HttpStatus.BAD_REQUEST.value());
        }

        StoredFile storedFile = findUserFile(user, oldFilename);
        Path newPath = fileStorageService.rename(Path.of(storedFile.getStoragePath()), newFilename);

        storedFile.setFilename(newFilename);
        storedFile.setStoragePath(newPath.toString());
        storedFile.setEditedAt(Instant.now());
        storedFileRepository.save(storedFile);
    }

    /**
     * Удаляет файл пользователя.
     */
    @Transactional
    public void deleteFile(User user, String filename) {
        validateFilename(filename);
        StoredFile storedFile = findUserFile(user, filename);
        fileStorageService.delete(Path.of(storedFile.getStoragePath()));
        storedFileRepository.delete(storedFile);
    }

    private StoredFile findUserFile(User user, String filename) {
        return storedFileRepository.findByUserAndFilename(user, filename)
                .orElseThrow(() -> new FileOperationException("Файл не найден", HttpStatus.BAD_REQUEST.value()));
    }

    private void validateFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new FileOperationException("Имя файла не указано", HttpStatus.BAD_REQUEST.value());
        }
    }

    private FileInfoResponse toResponse(StoredFile storedFile) {
        return new FileInfoResponse(
                storedFile.getFilename(),
                storedFile.getSize(),
                storedFile.getEditedAt().toEpochMilli()
        );
    }
}
