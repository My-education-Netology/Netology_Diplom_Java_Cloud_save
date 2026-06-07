# Описание реализации

Подробное описание архитектуры и логики Spring Boot REST-сервиса облачного хранилища.

---

## Общая архитектура

```
┌─────────────┐     HTTP + auth-token      ┌──────────────────────────────┐
│  Vue.js     │ ─────────────────────────► │  Spring Boot Backend :8080   │
│  FRONT :8081│                              │                              │
└─────────────┘                              │  Controller → Service → Repo │
                                             │         ↓                    │
                                             │  FileStorageService (диск)   │
                                             └──────────┬───────────────────┘
                                                        │
                                             ┌──────────▼───────────┐
                                             │  PostgreSQL :5432    │
                                             │  users, auth_tokens, │
                                             │  stored_files        │
                                             └──────────────────────┘
```

---

## Стек технологий

| Слой | Технология |
|------|------------|
| Framework | Spring Boot 3.5.0 |
| Web | Spring Web MVC |
| Persistence | Spring Data JPA + Hibernate |
| БД | PostgreSQL 16 |
| Миграции | Flyway |
| Безопасность паролей | BCrypt (`spring-security-crypto`) |
| Сборка | Gradle 8.14 |
| Контейнеризация | Docker, docker-compose |
| Unit-тесты | JUnit 5 + Mockito |
| Integration-тесты | Testcontainers + PostgreSQL |

---

## Структура пакетов

```
com.netology.cloud
├── CloudServiceBackendApplication    # Точка входа
├── controller/
│   ├── AuthController                # /login, /logout
│   └── FileController                # /list, /file
├── service/
│   ├── AuthService                   # Логика авторизации
│   └── FileService                   # CRUD файлов
├── repository/                       # Spring Data JPA
├── model/
│   ├── entity/                       # User, AuthToken, StoredFile
│   └── dto/                          # LoginResponse, FileInfoResponse...
├── security/
│   ├── AuthInterceptor               # Проверка токена
│   └── AuthContext                   # Константы и доступ к User
├── storage/
│   └── FileStorageService            # Запись/чтение на диск
├── config/
│   ├── WebConfig                     # CORS + регистрация интерцептора
│   ├── StorageProperties             # storage.files.path из yml
│   ├── CorsProperties                # cors.allowed-origins из yml
│   ├── PasswordConfig                # BCryptPasswordEncoder bean
│   └── DataInitializer               # Создание test/test
└── exception/
    └── GlobalExceptionHandler        # @RestControllerAdvice
```

---

## Схема базы данных

### users
| Колонка | Тип | Описание |
|---------|-----|----------|
| id | BIGSERIAL PK | |
| login | VARCHAR UNIQUE | Логин (email) |
| password_hash | VARCHAR | BCrypt-хеш |
| created_at | TIMESTAMP | |

### auth_tokens
| Колонка | Тип | Описание |
|---------|-----|----------|
| id | BIGSERIAL PK | |
| user_id | FK → users | Владелец |
| token | VARCHAR UNIQUE | Значение токена |
| active | BOOLEAN | Активен ли |
| created_at | TIMESTAMP | |

### stored_files
| Колонка | Тип | Описание |
|---------|-----|----------|
| id | BIGSERIAL PK | |
| user_id | FK → users | Владелец |
| filename | VARCHAR | Имя файла |
| size | BIGINT | Размер в байтах |
| storage_path | VARCHAR | Абсолютный путь на диске |
| created_at | TIMESTAMP | |
| edited_at | TIMESTAMP | Для FRONT (epoch ms) |

Миграция: `src/main/resources/db/migration/V1__init_schema.sql`

---

## Поток авторизации

1. `POST /login` — `AuthController` → `AuthService.login()`
2. Проверка login/password через `UserRepository` + `BCryptPasswordEncoder`
3. При ошибке — `LoginFailedException` → HTTP 400 + `{email:[], password:[]}`
4. При успехе — генерация UUID-токена, сохранение в `auth_tokens`
5. Ответ: `{"auth-token": "..."}`

Для защищённых эндпоинтов:
1. `AuthInterceptor` (кроме `/login`) читает заголовок `auth-token`
2. Убирает префикс `Bearer ` если есть
3. `AuthService.validateToken()` → `User` в `request.setAttribute`
4. При невалидном токене — `UnauthorizedException` → HTTP 401

`POST /logout` — деактивация токена (`active = false`).

---

## Поток работы с файлами

### Загрузка
1. `POST /file?filename=name` + multipart `file`
2. `FileService.uploadFile()` — проверка дубликата имени
3. `FileStorageService.store(userId, filename, file)` → `{storagePath}/{userId}/{filename}`
4. Запись метаданных в `stored_files`

### Список
1. `GET /list?limit=N`
2. `StoredFileRepository.findByUserOrderByEditedAtDesc()` с `PageRequest`
3. Маппинг в `[{filename, size, editedAt}]`

### Скачивание
1. `GET /file?filename=name`
2. Поиск записи по user + filename
3. `FileStorageService.loadAsResource()` → `ResponseEntity<Resource>`

### Переименование
1. `PUT /file?filename=old` + body `{name}` или `{filename}`
2. Переименование на диске + обновление БД

### Удаление
1. `DELETE /file?filename=name`
2. Удаление с диска + `storedFileRepository.delete()`

---

## Конфигурация (application.yml)

Все изменяемые параметры — в yml или переменных окружения:

```yaml
server.port: 8080
spring.datasource.*          # PostgreSQL
spring.flyway.enabled: true
spring.servlet.multipart.*   # до 100MB
storage.files.path           # путь к файлам
cors.allowed-origins         # адрес FRONT
```

Профиль `docker` (`application-docker.yml`) — для контейнера.

---

## Варианты запуска

### Вариант 1: Полный Docker (рекомендуется для сдачи)

```bash
docker compose up -d --build
```

Запускает PostgreSQL + backend. FRONT отдельно на хосте.

### Вариант 2: Только БД в Docker, backend локально

```bash
docker compose up -d postgres
cd cloud-service-backend
./gradlew bootRun
```

Удобно для отладки в IDE.

### Вариант 3: Сборка JAR

```bash
cd cloud-service-backend
./gradlew bootJar
java -jar build/libs/cloud-service-backend-0.0.1-SNAPSHOT.jar
```

### Вариант 4: Только тесты

```bash
cd cloud-service-backend
./gradlew test
```

---

## Тестовое покрытие

| Класс | Что проверяет |
|-------|---------------|
| `AuthServiceTest` | login, logout, validateToken, неверный пароль |
| `FileServiceTest` | list, upload, delete, rename, дубликат имени |
| `AuthControllerTest` | JSON-поле `auth-token` в ответе |
| `CloudServiceIntegrationTest` | Полный цикл: login → upload → list → download → delete |
| `CloudServiceBackendApplicationTests` | Поднятие Spring-контекста |

---

## Ключевые решения для совместимости с FRONT

1. **`@JsonProperty("auth-token")`** — Jackson сериализует поле с дефисом
2. **Bearer prefix** — интерцептор принимает оба формата
3. **`editedAt` в миллисекундах** — `Instant.toEpochMilli()`
4. **`RenameFileRequest`** — поддержка полей `name` (YAML) и `filename` (FRONT)
5. **CORS** — `allowCredentials(true)` + origin FRONT из yml
