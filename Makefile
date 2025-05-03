# Set the default target to show the available commands
.DEFAULT_GOAL := help

.PHONY: help build up down logs restart backend-shell frontend-shell

# Display help for available commands
help:
	@echo "Makefile commands:"
	@echo "  make build           - Build and start all containers (docker-compose up --build)"
	@echo "  make up              - Start containers (docker-compose up)"
	@echo "  make down            - Stop and remove containers (docker-compose down)"
	@echo "  make logs            - Tail logs of all containers (docker-compose logs -f)"
	@echo "  make restart         - Restart all containers (docker-compose restart)"
	@echo "  make backend-shell   - Open a shell in the backend container (requires bash)"
	@echo "  make frontend-shell  - Open a shell in the frontend container (requires bash)"

# Build (or rebuild) images and start containers
build:
	@echo "Building and starting containers..."
	docker-compose build

no-cache-build:
	@echo "Building and starting containers..."
	docker compose build --no-cache

# Start containers without rebuilding
up:
	@echo "Starting containers..."
	docker-compose up -d

# Stop and remove containers
down:
	@echo "Stopping and removing containers..."
	docker-compose down

# Tail logs from all containers
logs:
	@echo "Tailing logs..."
	docker-compose logs -f

# Restart containers
restart:
	@echo "Restarting containers..."
	docker-compose restart

# Open an interactive shell in the backend container
backend-shell:
	@echo "Opening shell in backend container..."
	docker-compose exec backend bash

# Open an interactive shell in the frontend container
frontend-shell:
	@echo "Opening shell in frontend container..."
	docker-compose exec frontend bash
