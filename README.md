# Netology Diplom — Облачное хранилище

Дипломный проект курса **Java-разработчик** (Нетология).

REST-сервис облачного хранилища на **Spring Boot** с авторизацией по токену, загрузкой/скачиванием файлов и интеграцией с готовым Vue.js-фронтендом от Нетологии.

**Задание:** [cloudservice.md](Docs/cloud-service/cloudservice.md)  
**Спецификация API:** [CloudServiceSpecification.yaml](Docs/cloud-service/CloudServiceSpecification.yaml)

---

## Статус проекта

| Этап | Статус |
|------|--------|
| Анализ задания и материалов | ✅ Завершён |
| Документация и Roadmap | ✅ Завершён |
| Реализация Spring Boot backend | ✅ Завершён |
| Unit- и интеграционные тесты | ✅ Завершён |
| Docker / docker-compose | ✅ Завершён |
| Проверка с FRONT | ⏳ Требует ручного запуска |

Подробный план работ: [ROADMAP.md](ROADMAP.md)

---

## Структура репозитория

```
.
├── README.md
├── ROADMAP.md
├── docker-compose.yml
├── Docs/cloud-service/          # Задание, FRONT, документация
└── cloud-service-backend/         # Spring Boot REST-сервис
    ├── Dockerfile
    ├── build.gradle
    └── src/
        ├── main/java/com/netology/cloud/
        │   ├── controller/      # AuthController, FileController
        │   ├── service/         # AuthService, FileService
        │   ├── repository/      # JPA-репозитории
        │   ├── model/           # Entity + DTO
        │   ├── security/        # AuthInterceptor
        │   ├── storage/         # FileStorageService
        │   ├── config/          # CORS, Flyway, DataInitializer
        │   └── exception/       # GlobalExceptionHandler
        └── test/                # Unit + Integration (Testcontainers)
```

---

## API-эндпоинты

| Метод | Путь | Auth | Описание |
|-------|------|------|----------|
| POST | `/login` | Нет | Авторизация → `{"auth-token":"..."}` |
| POST | `/logout` | Да | Выход, деактивация токена |
| GET | `/list?limit=N` | Да | Список файлов пользователя |
| POST | `/file?filename=` | Да | Загрузка (multipart, поле `file`) |
| GET | `/file?filename=` | Да | Скачивание файла |
| PUT | `/file?filename=` | Да | Переименование |
| DELETE | `/file?filename=` | Да | Удаление |

Заголовок авторизации: `auth-token: Bearer <token>`

---

## Запуск

### Предварительные требования

- Java 17+
- Docker и docker-compose
- Node.js ≥ 19.7.0 (для FRONT)

### 1. Backend + PostgreSQL

```bash
docker compose up -d --build
```

Backend: `http://localhost:8080`

### 2. FRONT

```bash
cd Docs/cloud-service/netology-diplom-frontend
npm install
```

В `.env` должен быть URL backend:
```
VUE_APP_BASE_URL=http://localhost:8080
```

```bash
npm run serve
```

FRONT: `http://localhost:8081` (если 8080 занят backend).

### 3. Тестовый вход

| Логин | Пароль |
|-------|--------|
| test | test |

---

## Локальная разработка (без Docker)

```bash
# Запустить только PostgreSQL
docker compose up -d postgres

# Запустить backend
cd cloud-service-backend
./gradlew bootRun
```

---

## Тесты

```bash
cd cloud-service-backend
./gradlew test
```

- **Unit-тесты** — AuthService, FileService (Mockito)
- **MockMvc** — AuthController
- **Интеграционные** — Testcontainers + PostgreSQL (требует Docker)

---

## Тестирование API (curl)

```bash
# Авторизация
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"login":"test","password":"test"}'

# Список файлов
curl "http://localhost:8080/list?limit=10" \
  -H "auth-token: Bearer <TOKEN>"

# Загрузка
curl -X POST "http://localhost:8080/file?filename=test.txt" \
  -H "auth-token: Bearer <TOKEN>" \
  -F "file=@/path/to/test.txt"
```

---

## История версий

| Версия | Дата | Описание |
|--------|------|----------|
| 0.1.0 | 2025-06-07 | Подготовка: анализ задания, материалы, документация |
| 1.0.0 | 2025-06-07 | Реализация backend, тесты, docker-compose |
