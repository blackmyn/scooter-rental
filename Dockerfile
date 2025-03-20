# Используем образ Maven с JDK 17
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Указываем рабочую директорию
WORKDIR /app1

# Копируем pom.xml для кэширования зависимостей
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходный код
COPY src ./src

# Собираем проект
RUN mvn clean package -DskipTests

# Создаём финальный контейнер на основе JDK
FROM openjdk:21-jdk-slim

# Указываем рабочую директорию
WORKDIR /app1

# Копируем JAR-файл из предыдущего этапа
COPY --from=build /app1/target/*.jar app.jar

# Открываем порт
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
