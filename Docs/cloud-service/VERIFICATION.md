# Результаты проверки работоспособности

Дата проверки: 2025-06-07

---

## 1. Unit- и интеграционные тесты

```bash
cd cloud-service-backend
./gradlew test
```

**Результат:** `BUILD SUCCESSFUL`

| Тест | Тип | Результат |
|------|-----|-----------|
| `AuthServiceTest` (5 тестов) | Mockito | ✅ |
| `FileServiceTest` (5 тестов) | Mockito | ✅ |
| `AuthControllerTest` (1 тест) | MockMvc | ✅ |
| `CloudServiceIntegrationTest` | Testcontainers | ⏭ Пропуск если Docker API недоступен из Gradle |
| `CloudServiceBackendApplicationTests` | Testcontainers | ⏭ Пропуск если Docker API недоступен из Gradle |

> Интеграционные тесты с `@EnabledIf` запускаются при доступном Docker. Полный цикл проверен через curl и docker-compose.

---

## 2. Docker Compose

```bash
docker compose up -d --build
docker compose ps
```

| Сервис | Статус | Порт |
|--------|--------|------|
| cloud-postgres | healthy | 5432 |
| cloud-backend | running | 8080 |

---

## 3. Проверка API (curl)

### Авторизация

```bash
curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"login":"test","password":"test"}'
```

**Ожидаемо:** `{"auth-token":"<строка>"}`  
**Факт:** ✅ HTTP 200, поле `auth-token` присутствует

### Полный жизненный цикл файла

| Шаг | Команда | HTTP | Результат |
|-----|---------|------|-----------|
| Список | `GET /list?limit=5` | 200 | JSON-массив |
| Загрузка | `POST /file?filename=diploma-test.txt` | 200 | Файл сохранён |
| Скачивание | `GET /file?filename=diploma-test.txt` | 200 | Содержимое совпадает |
| Переименование | `PUT /file` + `{"filename":"renamed-test.txt"}` | 200 | Имя обновлено |
| Удаление | `DELETE /file?filename=renamed-test.txt` | 200 | Файл удалён |
| Logout | `POST /logout` | 200 | Токен деактивирован |
| После logout | `GET /list` с тем же токеном | 401 | Доступ запрещён |
| Неверный пароль | `POST /login` password=wrong | 400 | Ошибка авторизации |
| CORS preflight | `OPTIONS /login` Origin :8081 | 200 | CORS настроен |

---

## 4. FRONT (Vue.js)

Backend полностью совместим. Запуск FRONT зависит от версии Node.js:

- **Node.js 22+** — ошибка `node-sass` (см. [TROUBLESHOOTING.md](TROUBLESHOOTING.md))
- **Node.js 19.7 – 20.x** — рекомендуется для FRONT от Нетологии

Проверка API через curl подтверждает все сценарии, которые использует FRONT: login, list, upload, download, rename, delete, logout.
