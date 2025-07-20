# Этап сборки
FROM eclipse-temurin:17-jdk-jammy as builder

WORKDIR /workspace/app

# Копируем Gradle-файлы (Kotlin DSL)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

# Делаем gradlew исполняемым и собираем проект
RUN chmod +x gradlew && ./gradlew build -x test

# Финальный образ
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Копируем собранный JAR
COPY --from=builder /workspace/app/build/libs/*.jar app.jar

# Параметры JVM
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Запуск от не-root пользователя
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]
