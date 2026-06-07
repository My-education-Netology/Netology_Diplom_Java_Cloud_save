# Установка и настройка окружения

Руководство по установке зависимостей на **Windows**, **macOS** и **Linux**.

---

## Необходимое ПО

| Компонент | Версия | Назначение |
|-----------|--------|------------|
| Java JDK | 17+ | Сборка и локальный запуск backend |
| Docker Desktop / Docker Engine | актуальная | PostgreSQL + backend в контейнерах |
| Docker Compose | v2+ | Оркестрация (`docker compose`) |
| Node.js | **19.7 – 20.x** (для FRONT) | Vue.js фронтенд от Нетологии |
| Git | актуальная | Клонирование репозитория |

> **Важно для FRONT:** в проекте Нетологии используется `node-sass`, который **не работает** на Node.js 22+. Используйте Node 19.7–20 через nvm/nvs (см. раздел Node.js ниже).

---

## macOS

### Java 17+

**Вариант A — Homebrew:**
```bash
brew install openjdk@17
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
java -version
```

**Вариант B — SDKMAN:**
```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 17.0.13-tem
```

### Docker

```bash
brew install --cask docker
# Запустить Docker Desktop из Applications
docker --version
docker compose version
```

### Node.js (для FRONT — версия 20)

```bash
# Рекомендуется nvm
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.1/install.sh | bash
source ~/.zshrc
nvm install 20
nvm use 20
node --version   # должно быть v20.x
```

### Git

```bash
brew install git
# или: xcode-select --install
```

---

## Windows

### Java 17+

1. Скачать [Eclipse Temurin JDK 17](https://adoptium.net/temurin/releases/?version=17)
2. Установить, добавить в PATH: `C:\Program Files\Eclipse Adoptium\jdk-17...\bin`
3. Проверка в PowerShell:
```powershell
java -version
```

### Docker

1. Установить [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop/)
2. Включить WSL2 backend (рекомендуется)
3. Проверка:
```powershell
docker --version
docker compose version
```

### Node.js 20 (для FRONT)

1. Установить [nvm-windows](https://github.com/coreybutler/nvm-windows/releases)
2. В PowerShell (от администратора):
```powershell
nvm install 20
nvm use 20
node --version
```

### Git

[Git for Windows](https://git-scm.com/download/win)

---

## Linux (Ubuntu / Debian)

### Java 17

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk
java -version
```

### Docker

```bash
sudo apt install -y docker.io docker-compose-plugin
sudo usermod -aG docker $USER
# Перелогиниться, затем:
docker --version
docker compose version
```

### Node.js 20 (для FRONT)

```bash
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install -y nodejs
node --version
```

### Git

```bash
sudo apt install -y git
```

---

## Клонирование и первичная настройка

```bash
git clone https://github.com/OrionFLASH/Netology_Diplom_Java_Cloud_save.git
cd Netology_Diplom_Java_Cloud_save
```

### Backend (Docker — рекомендуется)

```bash
docker compose up -d --build
```

Backend: `http://localhost:8080`

### Backend (локально без контейнера приложения)

```bash
docker compose up -d postgres          # только БД
cd cloud-service-backend
./gradlew bootRun                      # Windows: gradlew.bat bootRun
```

### FRONT

```bash
cd Docs/cloud-service/netology-diplom-frontend
nvm use 20                             # если установлен nvm
npm install
```

Проверить `.env`:
```
VUE_APP_BASE_URL=http://localhost:8080
```

```bash
npm run serve
```

FRONT: `http://localhost:8081` (порт 8080 занят backend).

### Тестовый пользователь

| Логин | Пароль |
|-------|--------|
| test | test |

Создаётся автоматически при первом запуске (`DataInitializer`).

---

## Переменные окружения backend

| Переменная | По умолчанию | Описание |
|------------|--------------|----------|
| `DB_HOST` | localhost | Хост PostgreSQL |
| `DB_PORT` | 5432 | Порт PostgreSQL |
| `DB_NAME` | cloudstorage | Имя БД |
| `DB_USER` | cloud | Пользователь БД |
| `DB_PASSWORD` | cloud | Пароль БД |
| `STORAGE_PATH` | ./storage/files | Каталог файлов на диске |
| `CORS_ORIGINS` | http://localhost:8081 | Разрешённые origins (через запятую) |
| `SPRING_PROFILES_ACTIVE` | — | `docker` для контейнера |

Пример для другого порта FRONT:
```bash
export CORS_ORIGINS=http://localhost:3000
./gradlew bootRun
```
