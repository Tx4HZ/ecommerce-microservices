.PHONY: build up down logs clean purge swagger

# Сборка всех сервисов
build:
	@echo "Building Gradle projects..."
	cd services/api-gateway && ./gradlew build -x test
	cd services/auth-service && ./gradlew build -x test
	cd services/user-service && ./gradlew build -x test
	@echo "Building Docker images..."
	docker-compose build

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
