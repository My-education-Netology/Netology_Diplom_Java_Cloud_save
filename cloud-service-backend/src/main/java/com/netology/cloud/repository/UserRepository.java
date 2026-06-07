package com.netology.cloud.repository;

import com.netology.cloud.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий пользователей.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);
}
