.PHONY: build rebuild rebuild-api-gateway rebuild-auth-service rebuild-user-service up down logs clean purge swagger status

# Полная сборка всех сервисов с нуля
build:
	@echo "Building Gradle projects from scratch..."
	cd services/api-gateway && ./gradlew clean build -x test
	cd services/auth-service && ./gradlew clean build -x test
	cd services/user-service && ./gradlew clean build -x test
	@echo "Building Docker images..."
	docker-compose build --no-cache

# Инкрементальная пересборка всех сервисов
rebuild:
	@echo "Incrementally rebuilding Gradle projects..."
	cd services/api-gateway && ./gradlew assemble -x test
	cd services/auth-service && ./gradlew assemble -x test
	cd services/user-service && ./gradlew assemble -x test
	@echo "Rebuilding Docker images..."
	docker-compose build

# Инкрементальная пересборка api-gateway
rebuild-api-gateway:
	@echo "Incrementally rebuilding api-gateway..."
	cd services/api-gateway && ./gradlew assemble -x test
	@echo "Rebuilding api-gateway Docker image..."
	docker-compose build api-gateway

# Инкрементальная пересборка auth-service
rebuild-auth-service:
	@echo "Incrementally rebuilding auth-service..."
	cd services/auth-service && ./gradlew assemble -x test
	@echo "Rebuilding auth-service Docker image..."
	docker-compose build auth-service

# Инкрементальная пересборка user-service
rebuild-user-service:
	@echo "Incrementally rebuilding user-service..."
	cd services/user-service && ./gradlew assemble -x test
	@echo "Rebuilding user-service Docker image..."
	docker-compose build user-service

# Запуск всех сервисов
up:
	@echo "Starting services..."
	docker-compose up -d

# Остановка сервисов
down:
	@echo "Stopping services..."
	docker-compose down

# Просмотр логов
logs:
	@echo "Showing logs..."
	docker-compose logs -f

# Очистка временных файлов
clean:
	@echo "Cleaning build directories..."
	@find . -name "build" -type d -prune -exec rm -rf {} +
	@find . -name "out" -type d -prune -exec rm -rf {} +

# Полная очистка (включая Docker)
purge: down clean
	@echo "Purging Docker resources..."
	@docker system prune -f --volumes
	@docker volume rm -f ecommerce-platform_postgres_data

# Открыть Swagger UI
swagger:
	@echo "OpenAPI/Swagger UI:"
	@echo "API Gateway: http://localhost:8080/swagger-ui.html"
	@echo "Auth Service: http://localhost:8081/swagger-ui.html"
	@echo "User Service: http://localhost:8082/swagger-ui.html"

# Проверка состояния сервисов
status:
	@docker-compose ps
