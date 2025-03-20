# Система управления прокатом электросамокатов

## Описание

Это RESTful API для системы управления прокатом электросамокатов.  Он позволяет регистрировать пользователей, управлять точками проката и самокатами, а также оформлять аренду.

## Требования

*   Установленный Docker и Docker Compose ([https://docs.docker.com/get-docker/](https://docs.docker.com/get-docker/))
*   Установленный Git (для клонирования репозитория)

## Установка и запуск

1.  **Клонируйте репозиторий:**

    ```
    git clone https://github.com/blackmyn/scooter-rental.git
    cd scooter-rental
    ```

2.  **Соберите и запустите приложение с помощью Docker Compose:**

    ```
    docker-compose up --build
    ```

    Эта команда:
    *   Соберет Docker-образ приложения (на основе `Dockerfile`).
    *   Запустит контейнер PostgreSQL.
    *   Запустит контейнер приложения, подключив его к базе данных.
    *   Выведет логи приложения в консоль.

### Аутентификация и авторизация

*   **Администратор:**
    *   Имя пользователя: `admin`
    *   Пароль: `password`
    *   Роль: `ADMIN`
*   **Менеджер:**
    *   Имя пользователя: `manager`
    *   Пароль: `password`
    *   Роль: `MANAGER`
*   **Пользователь:**
    *   Имя пользователя: `user`
    *   Пароль: `password`
    *   Роль: `USER`

### Примеры запросов

**1. Регистрация пользователя:**

*   **Метод:** POST
*   **URL:** `http://localhost:8080/api/auth/register`

    ```
    {
        "username": "newuser",
        "password": "password",
        "firstName": "New",
        "lastName": "User",
        "email": "new.user@example.com",
        "phoneNumber": "1234567890"
    }
    ```

**2. Получение информации о точке проката:**

*   **Метод:** GET
*   **URL:** `http://localhost:8080/api/rental-points/{id}`
*   **Пример:**  `http://localhost:8080/api/rental-points/1`

**3. Создание самоката:**

*   **Метод:** POST
*   **URL:** `http://localhost:8080/api/scooters`

    ```
    {
        "model": "Xiaomi M365 Pro",
        "serialNumber": "SN1234567890",
        "status": "AVAILABLE",
        "chargeLevel": 95,
        "mileage": 100.5,
        "rentalPointId": 1,
        "tariffId": 1
    }
    ```

## Конфигурация

Настройки приложения (подключение к базе данных, порт сервера, и т.д.) находятся в файле `application.properties`.

## Миграции базы данных

Для управления схемой базы данных используются миграции Flyway.  Файлы миграций находятся в директории `src/main/resources/db/migration`.  При запуске приложения Flyway автоматически применяет все новые миграции к базе данных.
