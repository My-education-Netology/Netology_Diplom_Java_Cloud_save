# Протокол взаимодействия FRONT ↔ BACKEND

Документ составлен на основе анализа исходников FRONT (`netology-diplom-frontend`) и OpenAPI-спецификации.  
При расхождениях **приоритет у поведения FRONT** — backend должен работать без доработок фронта.

---

## Базовый URL

| Компонент | URL | Порт |
|-----------|-----|------|
| BACKEND | `http://localhost:8080` | 8080 |
| FRONT | `http://localhost:8081` | 8081 (если 8080 занят backend) |

Переменная окружения FRONT (`.env`):
```
VUE_APP_BASE_URL=http://localhost:8080
```

> В спецификации YAML указан префикс `/cloud`, но FRONT обращается к эндпоинтам **без префикса**: `/login`, `/list`, `/file`, `/logout`.

---

## Авторизация

### POST `/login`

**Запрос:**
```json
{
  "login": "test",
  "password": "test"
}
```

**Успех (200):**
```json
{
  "auth-token": "уникальная-строка-токена"
}
```

> Поле **обязательно** называется `auth-token` (с дефисом). Иначе FRONT не сохранит токен.

**Ошибка (400):**
```json
{
  "email": ["Неправильно указана почта"],
  "password": ["Неправильно указан пароль"]
}
```

> FRONT маппит поле `login` на UI-поле «почта», но в ошибках ожидает ключ `email`.

---

### Заголовок авторизации

Все запросы кроме `/login` требуют заголовок:

```
auth-token: Bearer <значение_токена>
```

Источник: `src/api/httpClient.ts` — интерцептор добавляет префикс `Bearer `.

---

### POST `/logout`

**Заголовок:** `auth-token: Bearer <token>`

**Успех (200):** пустое тело или любой 2xx.

**Ошибка (401):** FRONT всё равно очищает локальный токен.

---

## Файлы

### GET `/list?limit={N}`

**Заголовок:** `auth-token: Bearer <token>`

**Успех (200):** массив объектов:
```json
[
  {
    "filename": "document.pdf",
    "size": 1258291,
    "editedAt": 1615231817551
  }
]
```

| Поле | Тип | Описание |
|------|-----|----------|
| `filename` | string | Полное имя файла с расширением |
| `size` | integer | Размер в байтах |
| `editedAt` | long | Unix timestamp в миллисекундах |

> FRONT вызывает `getFiles(3)` — limit=3 по умолчанию. Backend должен уважать параметр `limit`.

---

### POST `/file?filename={name}`

**Заголовок:** `auth-token: Bearer <token>`  
**Content-Type:** `multipart/form-data`

**Тело:** поле формы `file` (бинарные данные).

**Успех (200):** любой 2xx (FRONT после загрузки перезапрашивает `/list`).

---

### GET `/file?filename={name}`

**Заголовок:** `auth-token: Bearer <token>`

**Успех (200):** бинарное содержимое файла (`responseType: blob` на FRONT).

---

### PUT `/file?filename={oldName}`

**Заголовок:** `auth-token: Bearer <token>`  
**Content-Type:** `application/json`

**Тело (фактическое поведение FRONT):**
```json
{
  "filename": "новое_имя.ext"
}
```

> Расхождение со спецификацией YAML, где поле называется `name`. Backend должен принимать `filename`.

**Успех (200):** FRONT обновляет имя локально.

---

### DELETE `/file?filename={name}`

**Заголовок:** `auth-token: Bearer <token>`

**Успех (200):** FRONT перезапрашивает `/list`.

---

## CORS

FRONT работает с другого origin (`http://localhost:8081`). Backend должен разрешить:

```java
registry.addMapping("/**")
    .allowCredentials(true)
    .allowedOrigins("http://localhost:8081")
    .allowedMethods("*");
```

---

## Сводная таблица эндпоинтов

| Метод | Путь | Auth | Описание |
|-------|------|------|----------|
| POST | `/login` | Нет | Авторизация |
| POST | `/logout` | Да | Выход |
| GET | `/list` | Да | Список файлов |
| POST | `/file` | Да | Загрузка |
| GET | `/file` | Да | Скачивание |
| PUT | `/file` | Да | Переименование |
| DELETE | `/file` | Да | Удаление |

---

## Тестовые учётные данные

Для проверки с FRONT рекомендуется создать пользователя:

| login | password |
|-------|----------|
| test | test |

(совпадает с mock-backend из `netology-diplom-backend/server.js`)
