package com.netology.cloud.service;

import com.netology.cloud.exception.FileOperationException;
import com.netology.cloud.model.dto.FileInfoResponse;
import com.netology.cloud.model.dto.RenameFileRequest;
import com.netology.cloud.model.entity.StoredFile;
import com.netology.cloud.model.entity.User;
import com.netology.cloud.repository.StoredFileRepository;
import com.netology.cloud.storage.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit-тесты сервиса файловых операций.
 */
@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private StoredFileRepository storedFileRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private FileService fileService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setLogin("test");
    }

    @Test
    void listFiles_shouldReturnMappedResponses() {
        StoredFile file = new StoredFile();
        file.setFilename("doc.pdf");
        file.setSize(1024L);
        file.setEditedAt(Instant.parse("2021-03-08T20:50:17.551Z"));

        when(storedFileRepository.findByUserOrderByEditedAtDesc(eq(user), any(Pageable.class)))
                .thenReturn(List.of(file));

        List<FileInfoResponse> result = fileService.listFiles(user, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFilename()).isEqualTo("doc.pdf");
        assertThat(result.get(0).getSize()).isEqualTo(1024L);
    }

    @Test
    void uploadFile_shouldSaveMetadata(@TempDir Path tempDir) {
        MockMultipartFile multipart = new MockMultipartFile("file", "test.txt", "text/plain", "data".getBytes());
        Path stored = tempDir.resolve("test.txt");

        when(storedFileRepository.existsByUserAndFilename(user, "test.txt")).thenReturn(false);
        when(fileStorageService.store(1L, "test.txt", multipart)).thenReturn(stored);
        when(storedFileRepository.save(any(StoredFile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        fileService.uploadFile(user, "test.txt", multipart);

        verify(storedFileRepository).save(any(StoredFile.class));
    }

    @Test
    void deleteFile_shouldRemoveFromDbAndDisk(@TempDir Path tempDir) {
        StoredFile storedFile = new StoredFile();
        storedFile.setFilename("remove.txt");
        storedFile.setStoragePath(tempDir.resolve("remove.txt").toString());

        when(storedFileRepository.findByUserAndFilename(user, "remove.txt"))
                .thenReturn(Optional.of(storedFile));

        fileService.deleteFile(user, "remove.txt");

        verify(fileStorageService).delete(any(Path.class));
        verify(storedFileRepository).delete(storedFile);
    }

    @Test
    void renameFile_shouldUpdateFilename() {
        StoredFile storedFile = new StoredFile();
        storedFile.setFilename("old.txt");
        storedFile.setStoragePath("/storage/1/old.txt");

        RenameFileRequest request = new RenameFileRequest();
        request.setFilename("new.txt");

        when(storedFileRepository.findByUserAndFilename(user, "old.txt"))
                .thenReturn(Optional.of(storedFile));
        when(storedFileRepository.existsByUserAndFilename(user, "new.txt")).thenReturn(false);
        when(fileStorageService.rename(any(Path.class), eq("new.txt")))
                .thenReturn(Path.of("/storage/1/new.txt"));
        when(storedFileRepository.save(any(StoredFile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        fileService.renameFile(user, "old.txt", request);

        assertThat(storedFile.getFilename()).isEqualTo("new.txt");
    }

    @Test
    void uploadFile_shouldRejectDuplicateName() {
        MockMultipartFile multipart = new MockMultipartFile("file", "dup.txt", "text/plain", "data".getBytes());
        when(storedFileRepository.existsByUserAndFilename(user, "dup.txt")).thenReturn(true);

        assertThatThrownBy(() -> fileService.uploadFile(user, "dup.txt", multipart))
                .isInstanceOf(FileOperationException.class);
    }
}
