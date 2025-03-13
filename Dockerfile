# Используем официальный образ OpenJDK 17
FROM openjdk:21-jdk-slim

# Указываем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем файл jar в контейнер
COPY target/chat-platform-0.0.1-SNAPSHOT.jar app.jar

# Запускаем Spring Boot приложение
CMD ["java", "-jar", "app.jar"]