# Netology Diplom — Облачное хранилище

Дипломный проект курса **Java-разработчик** (Нетология).

REST-сервис облачного хранилища на **Spring Boot 3.5** с токен-авторизацией, CRUD файлов и интеграцией с Vue.js FRONT от Нетологии **без доработок фронта**.

| Материал | Ссылка |
|----------|--------|
| Задание | [cloudservice.md](Docs/cloud-service/cloudservice.md) |
| OpenAPI | [CloudServiceSpecification.yaml](Docs/cloud-service/CloudServiceSpecification.yaml) |
| Соответствие ТЗ | [COMPLIANCE.md](Docs/cloud-service/COMPLIANCE.md) |
| Результаты проверки | [VERIFICATION.md](Docs/cloud-service/VERIFICATION.md) |

---

## Статус проекта

| Этап | Статус |
|------|--------|
| Backend (все эндпоинты YAML) | ✅ |
| PostgreSQL + Flyway | ✅ |
| Docker / docker-compose | ✅ |
| Unit-тесты (Mockito) | ✅ |
| Интеграционные тесты (Testcontainers) | ✅ |
| Проверка curl | ✅ |
| FRONT (Vue.js) | ✅ Backend готов; FRONT требует Node 19–20 |

---

## Быстрый старт

```bash
# 1. Клонировать
git clone https://github.com/OrionFLASH/Netology_Diplom_Java_Cloud_save.git
cd Netology_Diplom_Java_Cloud_save

# 2. Backend + PostgreSQL
docker compose up -d --build

# 3. Проверка
curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"login":"test","password":"test"}'

# 4. FRONT (Node.js 20, см. INSTALL.md)
cd Docs/cloud-service/netology-diplom-frontend
npm install && npm run serve
# → http://localhost:8081, логин: test / test
```

---

## Структура репозитория

```
.
├── README.md
├── ROADMAP.md
├── docker-compose.yml
├── Docs/cloud-service/
│   ├── cloudservice.md           # Исходное задание
│   ├── COMPLIANCE.md             # Соответствие ТЗ
│   ├── VERIFICATION.md           # Результаты проверок
│   ├── IMPLEMENTATION.md         # Описание реализации
│   ├── INSTALL.md                # Установка (Win/macOS/Linux)
│   ├── TROUBLESHOOTING.md        # Устранение неполадок
│   ├── API_CONTRACT.md           # Протокол FRONT ↔ BACKEND
│   ├── ARCHITECTURE.md           # Архитектура и ER-диаграмма
│   └── netology-diplom-frontend/ # FRONT от Нетологии
└── cloud-service-backend/        # Spring Boot REST-сервис
```

---

## API-эндпоинты

| Метод | Путь | Auth | Описание |
|-------|------|------|----------|
| POST | `/login` | — | `{"login","password"}` → `{"auth-token":"..."}` |
| POST | `/logout` | ✅ | Деактивация токена |
| GET | `/list?limit=N` | ✅ | Список файлов `[{filename,size,editedAt}]` |
| POST | `/file?filename=` | ✅ | Загрузка (multipart, поле `file`) |
| GET | `/file?filename=` | ✅ | Скачивание |
| PUT | `/file?filename=` | ✅ | Переименование |
| DELETE | `/file?filename=` | ✅ | Удаление |

Заголовок: `auth-token: Bearer <token>`

---

## Варианты запуска

### Вариант A — Docker (рекомендуется)

```bash
docker compose up -d --build     # backend :8080 + postgres :5432
docker compose logs -f backend   # логи
docker compose down            # остановка
docker compose down -v         # остановка + удаление данных
```

### Вариант B — Локальная разработка

```bash
docker compose up -d postgres  # только БД
cd cloud-service-backend
./gradlew bootRun              # Windows: gradlew.bat bootRun
```

### Вариант C — Сборка JAR

```bash
cd cloud-service-backend
./gradlew bootJar
java -jar build/libs/cloud-service-backend-0.0.1-SNAPSHOT.jar
```

### Вариант D — FRONT

```bash
cd Docs/cloud-service/netology-diplom-frontend
# .env: VUE_APP_BASE_URL=http://localhost:8080
npm install
npm run serve
```

> FRONT займёт порт **8081**, если **8080** занят backend.  
> Для FRONT используйте **Node.js 19.7–20.x** (не 22+) — см. [TROUBLESHOOTING.md](Docs/cloud-service/TROUBLESHOOTING.md).

---

## Тесты

```bash
cd cloud-service-backend
./gradlew test
```

| Тип | Классы |
|-----|--------|
| Unit (Mockito) | `AuthServiceTest`, `FileServiceTest` |
| MockMvc | `AuthControllerTest` |
| Integration (Testcontainers) | `CloudServiceIntegrationTest` |

---

## Тестирование API

```bash
# Login
TOKEN=$(curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"login":"test","password":"test"}' \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['auth-token'])")

# Список
curl -s "http://localhost:8080/list?limit=10" \
  -H "auth-token: Bearer $TOKEN"

# Загрузка
curl -s -X POST "http://localhost:8080/file?filename=doc.txt" \
  -H "auth-token: Bearer $TOKEN" \
  -F "file=@./README.md"

# Скачивание
curl -s "http://localhost:8080/file?filename=doc.txt" \
  -H "auth-token: Bearer $TOKEN" -o downloaded.txt

# Удаление
curl -s -X DELETE "http://localhost:8080/file?filename=doc.txt" \
  -H "auth-token: Bearer $TOKEN"
```

Тестовый пользователь: **test** / **test**

---

## Документация

| Документ | Содержание |
|----------|------------|
| [IMPLEMENTATION.md](Docs/cloud-service/IMPLEMENTATION.md) | Архитектура, пакеты, потоки данных |
| [INSTALL.md](Docs/cloud-service/INSTALL.md) | Установка Java, Docker, Node на Win/macOS/Linux |
| [TROUBLESHOOTING.md](Docs/cloud-service/TROUBLESHOOTING.md) | Типичные ошибки и решения |
| [COMPLIANCE.md](Docs/cloud-service/COMPLIANCE.md) | Чеклист соответствия заданию |
| [VERIFICATION.md](Docs/cloud-service/VERIFICATION.md) | Результаты проверок |
| [ROADMAP.md](ROADMAP.md) | План разработки |

---

## Настройка (application.yml)

```yaml
server.port: 8080
spring.datasource.url: jdbc:postgresql://localhost:5432/cloudstorage
storage.files.path: ./storage/files
cors.allowed-origins: http://localhost:8081
```

Переопределение через переменные: `DB_HOST`, `DB_PORT`, `STORAGE_PATH`, `CORS_ORIGINS`.

---

## История версий

| Версия | Описание |
|--------|----------|
| 0.1.0 | Сохранены исходные материалы задания: cloudservice.md, YAML-спецификация, FRONT и mock-backend |
| 0.2.0 | Описана архитектура приложения и протокол взаимодействия FRONT ↔ BACKEND |
| 0.3.0 | Составлены README, ROADMAP и структура репозитория |
| 1.0.0 | Инициализирован Spring Boot 3.5 проект с Gradle Wrapper |
| 1.0.1 | Добавлен application.yml: PostgreSQL, Flyway, multipart, CORS, пути хранения |
| 1.1.0 | Реализованы JPA-сущности User, AuthToken, StoredFile |
| 1.1.1 | Добавлены JPA-репозитории с изоляцией данных по пользователю |
| 1.1.2 | Добавлена Flyway-миграция схемы БД (users, auth_tokens, stored_files) |
| 1.2.0 | Добавлены DTO: LoginResponse с полем auth-token, FileInfoResponse, ошибки login |
| 1.3.0 | Реализован POST /login с BCrypt и выдачей токена |
| 1.3.1 | Добавлены auth-интерцептор, POST /logout и настройка CORS для FRONT |
| 1.4.0 | Реализован FileStorageService — хранение бинарных файлов на диске |
| 1.4.1 | Реализованы эндпоинты GET /list, POST/GET/PUT/DELETE /file |
| 1.4.2 | Добавлен GlobalExceptionHandler и автосоздание пользователя test/test |
| 1.5.0 | Добавлены Dockerfile и docker-compose (backend + PostgreSQL 16) |
| 1.6.0 | Unit-тесты AuthService (Mockito): login, logout, validateToken |
| 1.6.1 | Unit-тесты FileService (Mockito): upload, list, rename, delete |
| 1.6.2 | MockMvc-тест AuthController: проверка поля auth-token в ответе |
| 1.6.3 | Интеграционные тесты с Testcontainers (полный цикл файловых операций) |
| 1.7.0 | Обновлены README и ROADMAP после завершения реализации backend |
| 1.8.0 | Добавлены COMPLIANCE, VERIFICATION, INSTALL, TROUBLESHOOTING, IMPLEMENTATION |
| 1.8.1 | Финальная проверка API (curl), соответствие ТЗ, публикация на GitHub |
