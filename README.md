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
| Реализация Spring Boot backend | ⏳ Запланировано |
| Unit- и интеграционные тесты | ⏳ Запланировано |
| Docker / docker-compose | ⏳ Запланировано |
| Проверка с FRONT | ⏳ Запланировано |

Подробный план работ: [ROADMAP.md](ROADMAP.md)

---

## Структура репозитория

```
.
├── README.md                          # Этот файл
├── ROADMAP.md                         # План разработки со статусами
├── .gitignore
├── Docs/
│   └── cloud-service/
│       ├── cloudservice.md            # Исходное задание Нетологии
│       ├── CloudServiceSpecification.yaml
│       ├── API_CONTRACT.md            # Анализ протокола FRONT ↔ BACKEND
│       ├── ARCHITECTURE.md            # Архитектура и схема БД
│       ├── netology-diplom-frontend/  # Исходный FRONT (Vue.js, не изменять)
│       └── netology-diplom-backend/   # Эталонный mock-backend (справочно)
└── cloud-service-backend/             # Spring Boot REST-сервис (будет создан)
```

---

## Описание задачи

Разработать REST-сервис, который:

1. Авторизует пользователей (`POST /login`, `POST /logout`)
2. Возвращает список файлов пользователя (`GET /list`)
3. Загружает файлы (`POST /file`)
4. Скачивает файлы (`GET /file`)
5. Переименовывает файлы (`PUT /file`)
6. Удаляет файлы (`DELETE /file`)

Все запросы (кроме `/login`) авторизованы заголовком `auth-token`.  
Настройки — из `application.yml`. Данные пользователей и метаданные файлов — в PostgreSQL.

---

## Требования к реализации (из задания)

- Spring Boot + Gradle
- Docker / docker-compose
- Unit-тесты (Mockito)
- Интеграционные тесты (Testcontainers)
- Код на GitHub
- Совместимость с FRONT без доработок

---

## Документация

| Документ | Описание |
|----------|----------|
| [ROADMAP.md](ROADMAP.md) | Поэтапный план с чеклистами и стратегией коммитов |
| [API_CONTRACT.md](Docs/cloud-service/API_CONTRACT.md) | Детальный протокол взаимодействия с FRONT |
| [ARCHITECTURE.md](Docs/cloud-service/ARCHITECTURE.md) | Архитектура, схема БД, диаграммы |

---

## Запуск (после реализации backend)

### Предварительные требования

- Java 17+
- Docker и docker-compose
- Node.js ≥ 19.7.0 (для FRONT)

### 1. Backend + PostgreSQL

```bash
docker-compose up -d
```

Backend будет доступен на `http://localhost:8080`.

### 2. FRONT

```bash
cd Docs/cloud-service/netology-diplom-frontend
npm install
```

Убедитесь, что в `.env` указан URL backend:
```
VUE_APP_BASE_URL=http://localhost:8080
```

```bash
npm run serve
```

FRONT откроется на `http://localhost:8081` (если порт 8080 занят backend).

### 3. Тестовый вход

| Логин | Пароль |
|-------|--------|
| test | test |

---

## Тестирование API (curl)

```bash
# Авторизация
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"login":"test","password":"test"}'

# Список файлов (подставить токен из ответа login)
curl http://localhost:8080/list?limit=10 \
  -H "auth-token: Bearer <TOKEN>"

# Загрузка файла
curl -X POST "http://localhost:8080/file?filename=test.txt" \
  -H "auth-token: Bearer <TOKEN>" \
  -F "file=@/path/to/test.txt"
```

---

## История версий

| Версия | Дата | Описание |
|--------|------|----------|
| 0.1.0 | 2025-06-07 | Подготовка: анализ задания, материалы, документация, Roadmap |
