.PHONY: run infra-up infra-down infra-status module-start module-stop module-restart module-status \
        redis-up redis-down redis-init redis-sessions

run:
	@echo "Running ./gradlew buildDockerImage"
	@./gradlew buildDockerImage
	@echo "Running docker-compose up -d"
	@docker-compose up -d

infra-up:
	@./scripts/devctl.sh infra up

infra-down:
	@./scripts/devctl.sh infra down

infra-status:
	@./scripts/devctl.sh infra status

module-start:./scripts/devctl.sh
	@./scripts/devctl.sh module start $(module)

module-stop:
	@./scripts/devctl.sh module stop $(module)

module-restart:
	@./scripts/devctl.sh module restart $(module)

module-status:
	@./scripts/devctl.sh module status $(module)

redis-up:
	@./scripts/devctl.sh redis up

redis-init:
	@./scripts/devctl.sh redis init

redis-down:
	@./scripts/devctl.sh redis down

redis-sessions:
	@./scripts/devctl.sh redis sessions
