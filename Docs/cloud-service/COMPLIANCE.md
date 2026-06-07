# Соответствие решения требованиям задания

Источник: [cloudservice.md](cloudservice.md), [CloudServiceSpecification.yaml](CloudServiceSpecification.yaml)

---

## Требования к приложению

| Требование | Статус | Реализация |
|------------|--------|------------|
| REST-интерфейс для интеграции с FRONT | ✅ | `AuthController`, `FileController` — эндпоинты без префикса `/cloud` |
| Вывод списка файлов | ✅ | `GET /list?limit=N` → `[{filename, size, editedAt}]` |
| Добавление файла | ✅ | `POST /file?filename=` + multipart `file` |
| Удаление файла | ✅ | `DELETE /file?filename=` |
| Авторизация | ✅ | `POST /login` → `{"auth-token":"..."}` |
| Все методы из YAML | ✅ | Дополнительно: `POST /logout`, `GET /file`, `PUT /file` |
| Настройки из yml | ✅ | `application.yml`, `application-docker.yml` |
| Пользователи и данные в БД | ✅ | PostgreSQL: `users`, `auth_tokens`, `stored_files` |
| FRONT без доработок | ✅ | Совместимость по `auth-token`, Bearer, CORS, форматам ответов |

---

## Требования к реализации

| Требование | Статус | Где |
|------------|--------|-----|
| Spring Boot | ✅ | Spring Boot 3.5.0 |
| Gradle | ✅ | `cloud-service-backend/build.gradle`, Gradle Wrapper |
| Docker + docker-compose | ✅ | `Dockerfile`, `docker-compose.yml` |
| Код на GitHub | ✅ | Репозиторий `OrionFLASH/Netology_Diplom_Java_Cloud_save` |
| Unit-тесты (Mockito) | ✅ | `AuthServiceTest`, `FileServiceTest` |
| Интеграционные тесты (Testcontainers) | ✅ | `CloudServiceIntegrationTest`, `CloudServiceBackendApplicationTests` |

---

## Шаги реализации (из задания)

| Шаг | Статус | Документ / артефакт |
|-----|--------|---------------------|
| Изучить протокол FRONT ↔ BACKEND | ✅ | [API_CONTRACT.md](API_CONTRACT.md) |
| Схема приложений | ✅ | [ARCHITECTURE.md](ARCHITECTURE.md) — Mermaid-диаграммы |
| Описать архитектуру, БД, хранение файлов | ✅ | [ARCHITECTURE.md](ARCHITECTURE.md) |
| Репозиторий на GitHub | ✅ | Настроен remote `origin` |
| Spring Boot приложение | ✅ | `cloud-service-backend/` |
| Тест curl/Postman | ✅ | [VERIFICATION.md](VERIFICATION.md) |
| Тест с FRONT | ⚠️ | Backend готов; FRONT требует Node.js 19–20 (см. [INSTALL.md](INSTALL.md)) |
| README.md | ✅ | [README.md](../../README.md) |

---

## Соответствие OpenAPI (YAML)

| Метод | Путь | Реализован | Примечание |
|-------|------|------------|------------|
| POST | `/login` | ✅ | Поле `auth-token` с дефисом |
| POST | `/logout` | ✅ | Деактивация токена в БД |
| GET | `/list` | ✅ | + поле `editedAt` для FRONT |
| POST | `/file` | ✅ | Multipart, поле `file` |
| GET | `/file` | ✅ | Скачивание бинарного содержимого |
| PUT | `/file` | ✅ | Принимает `name` и `filename` в теле |
| DELETE | `/file` | ✅ | Удаление с диска и из БД |

---

## Совместимость с FRONT (критические точки)

| Точка | Требование FRONT | Наша реализация |
|-------|------------------|-----------------|
| Токен в ответе login | `auth-token` | `@JsonProperty("auth-token")` в `LoginResponse` |
| Заголовок запросов | `auth-token: Bearer <token>` | `AuthInterceptor` обрезает префикс `Bearer ` |
| Ошибки login | `{email:[], password:[]}` | `LoginErrorResponse` при HTTP 400 |
| Список файлов | `filename`, `size`, `editedAt` | `FileInfoResponse` |
| Переименование | `{filename: "..."}` | `RenameFileRequest.resolveNewName()` |
| CORS | Origin FRONT | `cors.allowed-origins` в yml, по умолчанию `:8081` |

---

## Результаты автоматической проверки (2025-06-07)

```
POST /login          → 200, auth-token присутствует
GET  /list           → 200, JSON-массив
POST /file           → 200
GET  /file           → 200, содержимое файла
PUT  /file           → 200, переименование
DELETE /file         → 200
POST /logout         → 200
GET  /list (после logout) → 401
POST /login (неверный пароль) → 400
OPTIONS (CORS)       → 200
./gradlew test       → BUILD SUCCESSFUL (11 unit + 2 integration skipped без Docker API)
docker compose up  → backend :8080, postgres :5432
```
