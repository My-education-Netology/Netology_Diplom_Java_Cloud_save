package com.netology.cloud;

import com.netology.cloud.integration.PostgresTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Проверка поднятия контекста приложения.
 */
@SpringBootTest
@EnabledIf("com.netology.cloud.integration.PostgresTestSupport#isDockerAvailable")
class CloudServiceBackendApplicationTests {

    @BeforeAll
    static void startPostgres() {
        PostgresTestSupport.ensureStarted();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", PostgresTestSupport.POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", PostgresTestSupport.POSTGRES::getUsername);
        registry.add("spring.datasource.password", PostgresTestSupport.POSTGRES::getPassword);
        registry.add("storage.files.path", () -> "./target/test-storage");
    }

    @Test
    void contextLoads() {
    }
}
