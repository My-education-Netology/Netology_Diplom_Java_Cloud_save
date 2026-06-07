# Устранение неполадок

Типичные проблемы при запуске и проверке дипломного проекта.

---

## Backend

### Порт 8080 занят

**Симптом:** `Bind for 0.0.0.0:8080 failed: port is already allocated`

**Решение:**
```bash
# macOS/Linux — найти процесс
lsof -i :8080
kill -9 <PID>

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

Либо изменить порт в `application.yml`:
```yaml
server:
  port: 9090
```
И обновить `VUE_APP_BASE_URL` во FRONT.

---

### Backend не подключается к PostgreSQL

**Симптом:** `Connection refused` или `password authentication failed`

**Решение:**
1. Убедиться, что postgres запущен:
   ```bash
   docker compose ps
   docker compose logs postgres
   ```
2. Дождаться статуса `healthy` перед стартом backend
3. Проверить credentials в `application.yml` / `docker-compose.yml`

---

### Flyway migration failed

**Симптом:** `Validate failed: Migrations have failed validation`

**Решение:**
```bash
docker compose down -v    # удалит volumes (данные БД!)
docker compose up -d --build
```

---

### 401 на всех запросах кроме login

**Симптом:** FRONT перенаправляет на страницу входа

**Причины и решения:**

| Причина | Решение |
|---------|---------|
| Неверный формат заголовка | FRONT шлёт `auth-token: Bearer <token>` — backend это поддерживает |
| Токен истёк (logout) | Повторный login |
| Поле `auth-token` в ответе login | Проверить в Network: ответ должен содержать `"auth-token"`, не `authToken` |

---

### CORS error в браузере

**Симптом:** `Access to XMLHttpRequest blocked by CORS policy`

**Решение:**
1. Узнать порт FRONT в терминале (`npm run serve` → `http://localhost:8081`)
2. Добавить origin в `application.yml`:
   ```yaml
   cors:
     allowed-origins: http://localhost:8081
   ```
3. Или через переменную:
   ```bash
   CORS_ORIGINS=http://localhost:8081 docker compose up -d --build
   ```

---

### Файл не загружается (400)

**Симптом:** Ошибка при upload

**Проверить:**
- Query-параметр `filename` указан
- Multipart-поле называется `file` (не `filename` в теле)
- Заголовок `auth-token` присутствует
- Файл с таким именем ещё не существует у пользователя

---

## FRONT (Vue.js)

### node-sass / Unsupported runtime

**Симптом:**
```
Node Sass does not yet support your current environment:
OS X Unsupported architecture (arm64) with Unsupported runtime (127)
```

**Причина:** FRONT от Нетологии использует устаревший `node-sass`, несовместимый с Node.js 22+.

**Решение (рекомендуется):**
```bash
nvm install 20
nvm use 20
cd Docs/cloud-service/netology-diplom-frontend
rm -rf node_modules package-lock.json
npm install
npm run serve
```

**Альтернатива (без смены Node):** замена `node-sass` на `sass` в `package.json` FRONT — **не требуется для сдачи**, если API проверен через curl/Postman. Backend полностью совместим.

---

### npm install падает на node-gyp

**Симптом:** `gyp ERR!` при сборке node-sass

**Решение:**
- Использовать Node 20 (см. выше)
- macOS: `xcode-select --install`
- Windows: установить [windows-build-tools](https://github.com/nodejs/node-gyp#on-windows)

---

### FRONT на порту 8080 вместо 8081

**Симптом:** Конфликт с backend

**Решение:** Сначала запустить backend (`docker compose up`), затем FRONT. Vue CLI автоматически выберет 8081, если 8080 занят. Проверить порт в выводе `npm run serve`.

---

### Логин не проходит, но curl работает

**Проверить в DevTools → Network:**
1. URL запроса: `http://localhost:8080/login`
2. Ответ 200 с полем `auth-token`
3. Нет CORS-ошибок
4. `VUE_APP_BASE_URL` в `.env` корректен (без слэша в конце)

После изменения `.env` — перезапустить `npm run serve`.

---

## Docker

### Docker Desktop не запущен

**Симптом:** `Cannot connect to the Docker daemon`

**Решение:** Запустить Docker Desktop (macOS/Windows) или `sudo systemctl start docker` (Linux).

---

### Образ не собирается на ARM (Apple Silicon)

**Симптом:** `no match for platform in manifest: eclipse-temurin:17-jre-alpine`

**Решение:** В проекте используется `eclipse-temurin:17-jre` (не alpine) — уже исправлено в `Dockerfile`.

---

## Тесты

### Testcontainers: Could not find Docker environment

**Симптом:** Интеграционные тесты падают при `./gradlew test`

**Решение:**
1. Убедиться, что Docker запущен
2. Тесты помечены `@EnabledIf` — пропускаются если Docker API недоступен из Gradle
3. Unit-тесты (Mockito) работают без Docker:
   ```bash
   ./gradlew test --tests "com.netology.cloud.service.*"
   ```

---

## Gradle

### Java toolchain not found

**Симптом:** `Cannot find a Java installation matching languageVersion=17`

**Решение:** Установить JDK 17+ (см. [INSTALL.md](INSTALL.md)) или задать `JAVA_HOME`:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)   # macOS
./gradlew test
```

---

## Быстрая диагностика

```bash
# 1. Docker
docker compose ps

# 2. Backend
curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"login":"test","password":"test"}'

# 3. Тесты
cd cloud-service-backend && ./gradlew test

# 4. Версия Node (для FRONT)
node --version   # нужно 19.7–20.x
```
