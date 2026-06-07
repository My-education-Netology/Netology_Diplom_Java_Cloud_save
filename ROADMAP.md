# Roadmap — Диплом «Облачное хранилище»

> Статусы: `[v]` — сделано · `[w]` — в работе · `[ ]` — не сделано · `[x]` — отменено

Источник задания: [cloudservice.md](Docs/cloud-service/cloudservice.md)  
Спецификация API: [CloudServiceSpecification.yaml](Docs/cloud-service/CloudServiceSpecification.yaml)

---

## Фаза 0. Подготовка и анализ

| # | Задача | Статус |
|---|--------|--------|
| 0.1 | Изучить текст задания и OpenAPI-спецификацию | `[v]` |
| 0.2 | Скачать и сохранить FRONT в `Docs/cloud-service/netology-diplom-frontend` | `[v]` |
| 0.3 | Сохранить эталонный mock-backend в `Docs/cloud-service/netology-diplom-backend` | `[v]` |
| 0.4 | Проанализировать протокол FRONT ↔ BACKEND (см. `Docs/cloud-service/API_CONTRACT.md`) | `[v]` |
| 0.5 | Описать архитектуру (см. `Docs/cloud-service/ARCHITECTURE.md`) | `[v]` |
| 0.6 | Составить Roadmap и первичную документацию | `[v]` |
| 0.7 | Настроить `.gitignore`, структуру репозитория | `[v]` |

---

## Фаза 1. Инициализация Spring Boot-проекта

| # | Задача | Статус |
|---|--------|--------|
| 1.1 | Создать модуль `cloud-service-backend` (Gradle, Spring Boot 3.x, Java 17+) | `[ ]` |
| 1.2 | Настроить `application.yml` — порт, БД, пути хранения файлов, CORS | `[ ]` |
| 1.3 | Подключить зависимости: Spring Web, Spring Data JPA, PostgreSQL, Lombok, Validation | `[ ]` |
| 1.4 | Создать базовую структуру пакетов (`controller`, `service`, `repository`, `model`, `config`, `security`, `exception`) | `[ ]` |
| 1.5 | Добавить `Dockerfile` и `docker-compose.yml` (app + PostgreSQL) | `[ ]` |

**Коммиты (план):**
- `init: scaffold Spring Boot project with Gradle`
- `config: add application.yml and docker-compose`
- `build: add Dockerfile for backend service`

---

## Фаза 2. Модель данных и миграции

| # | Задача | Статус |
|---|--------|--------|
| 2.1 | Entity `User` — id, login, password (BCrypt hash) | `[ ]` |
| 2.2 | Entity `AuthToken` — token, userId, createdAt, active | `[ ]` |
| 2.3 | Entity `StoredFile` — id, userId, filename, size, storagePath, createdAt, editedAt | `[ ]` |
| 2.4 | JPA-репозитории для всех сущностей | `[ ]` |
| 2.5 | Flyway/Liquibase миграции или `schema.sql` + тестовые пользователи | `[ ]` |
| 2.6 | DataInitializer — создать пользователя `test` / `test` для проверки с FRONT | `[ ]` |

**Коммиты (план):**
- `feat: add User and AuthToken entities`
- `feat: add StoredFile entity and repositories`
- `feat: add database migrations and seed data`

---

## Фаза 3. Авторизация

| # | Задача | Статус |
|---|--------|--------|
| 3.1 | `POST /login` — проверка login/password, ответ `{"auth-token": "..."}` | `[ ]` |
| 3.2 | `POST /logout` — деактивация токена | `[ ]` |
| 3.3 | Фильтр/интерцептор: чтение заголовка `auth-token` с префиксом `Bearer ` | `[ ]` |
| 3.4 | Обработка 401 для неавторизованных запросов | `[ ]` |
| 3.5 | Обработка 400 при неверных credentials в формате `{email: [], password: []}` | `[ ]` |
| 3.6 | Настройка CORS для `http://localhost:8081` (адрес FRONT) | `[ ]` |

**Коммиты (план):**
- `feat: implement login endpoint with auth-token response`
- `feat: add auth filter and logout endpoint`
- `fix: handle Bearer prefix in auth-token header`
- `config: add CORS for frontend origin`

---

## Фаза 4. Операции с файлами

| # | Задача | Статус |
|---|--------|--------|
| 4.1 | `GET /list?limit=N` — список файлов текущего пользователя | `[ ]` |
| 4.2 | `POST /file?filename=...` — загрузка multipart (`file`) | `[ ]` |
| 4.3 | `GET /file?filename=...` — скачивание (blob) | `[ ]` |
| 4.4 | `PUT /file?filename=...` — переименование | `[ ]` |
| 4.5 | `DELETE /file?filename=...` — удаление файла и записи в БД | `[ ]` |
| 4.6 | Хранение бинарных данных на диске (путь из `application.yml`) | `[ ]` |
| 4.7 | Изоляция файлов по пользователям | `[ ]` |

**Формат ответа `/list` (требование FRONT):**
```json
[
  {
    "filename": "document.pdf",
    "size": 1024000,
    "editedAt": 1615231817551
  }
]
```

**Коммиты (план):**
- `feat: implement file list endpoint`
- `feat: implement file upload and storage`
- `feat: implement file download, rename and delete`
- `fix: align list response fields with frontend expectations`

---

## Фаза 5. Обработка ошибок и валидация

| # | Задача | Статус |
|---|--------|--------|
| 5.1 | `@ControllerAdvice` — единый обработчик ошибок | `[ ]` |
| 5.2 | Ответы 400/401/500 в формате, совместимом с FRONT | `[ ]` |
| 5.3 | Валидация входных параметров (filename, limit, multipart) | `[ ]` |

**Коммиты (план):**
- `feat: add global exception handler`
- `fix: improve validation error responses`

---

## Фаза 6. Тестирование

| # | Задача | Статус |
|---|--------|--------|
| 6.1 | Unit-тесты сервисов (Mockito) — AuthService, FileService | `[ ]` |
| 6.2 | Unit-тесты контроллеров (MockMvc + Mockito) | `[ ]` |
| 6.3 | Интеграционные тесты (Testcontainers + PostgreSQL) | `[ ]` |
| 6.4 | Ручное тестирование через curl/Postman | `[ ]` |
| 6.5 | Интеграционное тестирование с FRONT | `[ ]` |

**Коммиты (план):**
- `test: add unit tests for auth service`
- `test: add unit tests for file service`
- `test: add integration tests with testcontainers`
- `fix: resolve issues found during manual testing`

---

## Фаза 7. Документация и деплой

| # | Задача | Статус |
|---|--------|--------|
| 7.1 | Актуализировать `README.md` — запуск, API, тестирование | `[ ]` |
| 7.2 | Диаграмма архитектуры (Mermaid в `Docs/`) | `[ ]` |
| 7.3 | Инструкция запуска FRONT + BACKEND | `[ ]` |
| 7.4 | Финальная проверка docker-compose | `[ ]` |
| 7.5 | Push в GitHub | `[ ]` |

**Коммиты (план):**
- `docs: update README with run instructions`
- `docs: add architecture diagram`
- `chore: final docker-compose adjustments`

---

## Планируемая структура проекта (после реализации)

```
Netology_Diplom_Java_Cloud_save/
├── README.md
├── ROADMAP.md
├── .gitignore
├── docker-compose.yml              # оркестрация backend + postgres
├── Docs/
│   └── cloud-service/
│       ├── cloudservice.md         # исходное задание
│       ├── CloudServiceSpecification.yaml
│       ├── API_CONTRACT.md         # анализ протокола FRONT↔BACK
│       ├── ARCHITECTURE.md         # архитектура решения
│       ├── netology-diplom-frontend/  # исходный FRONT (не изменять)
│       └── netology-diplom-backend/   # эталонный mock (справочно)
└── cloud-service-backend/          # Spring Boot REST-сервис
    ├── build.gradle.kts
    ├── Dockerfile
    └── src/
        ├── main/
        │   ├── java/.../
        │   └── resources/
        │       └── application.yml
        └── test/
```

---

## Критические замечания по совместимости с FRONT

1. **Поле токена** — строго `auth-token` (с дефисом), не `authToken`.
2. **Заголовок авторизации** — FRONT отправляет `auth-token: Bearer <token>`.
3. **Ответ `/list`** — массив объектов с полями `filename`, `size`, `editedAt` (не `name`).
4. **Ошибки логина** — `{ "email": ["..."], "password": ["..."] }` при HTTP 400.
5. **Переименование** — FRONT отправляет `{ "filename": "новое_имя.ext" }` в теле PUT (расхождение со спецификацией, где поле `name`).
6. **Порты** — BACKEND на `8080`, FRONT на `8081` (если 8080 занят backend'ом).
7. **Префикс `/cloud`** в YAML — FRONT его **не использует**; эндпоинты на корневом уровне.

---

## Стратегия коммитов

Коммиты делаются небольшими, логически завершёнными порциями — как при обычной разработке:

| Тип | Примеры сообщений |
|-----|-------------------|
| Инициализация | `init: ...`, `config: ...` |
| Функционал | `feat: implement login endpoint` |
| Тесты | `test: add unit tests for file service` |
| Исправления | `fix: handle Bearer prefix in auth header` |
| Документация | `docs: add API contract analysis` |
| Инфраструктура | `build: add Dockerfile` |

**Правило:** один коммит = одна логическая единица работы. Исправления ошибок — отдельные коммиты с префиксом `fix:`.
