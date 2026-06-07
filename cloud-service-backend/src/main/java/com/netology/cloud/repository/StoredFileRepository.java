package com.netology.cloud.repository;

import com.netology.cloud.model.entity.StoredFile;
import com.netology.cloud.model.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий метаданных загруженных файлов.
 */
public interface StoredFileRepository extends JpaRepository<StoredFile, Long> {

    List<StoredFile> findByUserOrderByEditedAtDesc(User user, Pageable pageable);

    Optional<StoredFile> findByUserAndFilename(User user, String filename);

    boolean existsByUserAndFilename(User user, String filename);
}
