package com.netology.cloud.repository;

import com.netology.cloud.model.entity.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий токенов авторизации.
 */
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByTokenAndActiveTrue(String token);
}
