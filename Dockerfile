# Используем базовый образ OpenJDK
FROM openjdk:21-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем JAR-файл в контейнер
COPY target/chat-platform-0.0.1-SNAPSHOT.jar app.jar

COPY src/main/resources/db/migration /app/db/migration

# Указываем команду для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]