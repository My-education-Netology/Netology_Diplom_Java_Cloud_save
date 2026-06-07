package com.netology.cloud.config;

import com.netology.cloud.model.entity.User;
import com.netology.cloud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Создаёт тестового пользователя test/test при первом запуске.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private static final String TEST_LOGIN = "test";
    private static final String TEST_PASSWORD = "test";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByLogin(TEST_LOGIN).isEmpty()) {
            User user = new User();
            user.setLogin(TEST_LOGIN);
            user.setPasswordHash(passwordEncoder.encode(TEST_PASSWORD));
            userRepository.save(user);
            log.info("Создан тестовый пользователь: login={}", TEST_LOGIN);
        }
    }
}
