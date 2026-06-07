package com.netology.cloud.integration;

import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Общая поддержка PostgreSQL-контейнера для интеграционных тестов.
 */
public final class PostgresTestSupport {

    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("cloudstorage")
            .withUsername("cloud")
            .withPassword("cloud");

    private PostgresTestSupport() {
    }

    public static boolean isDockerAvailable() {
        try {
            return DockerClientFactory.instance().isDockerAvailable();
        } catch (Exception ex) {
            return false;
        }
    }

    public static void ensureStarted() {
        if (!POSTGRES.isRunning()) {
            POSTGRES.start();
        }
    }
}
