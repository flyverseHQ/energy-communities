# Energy Communities

This project implements a distributed system for simulating an energy community, where energy production and consumption are processed and visualized.

The system follows a modular architecture with a REST API backend, a JavaFX desktop client, and supporting infrastructure using Docker.

## Components

* backend-api: Spring Boot REST API
* gui: JavaFX client
* PostgreSQL (Docker)
* RabbitMQ (Docker)

## Start infrastructure

```bash
docker compose up -d
```

## Run backend

```bash
cd backend-api
mvn spring-boot:run
```

## API endpoints

```text
GET http://localhost:8080/energy/current
GET http://localhost:8080/energy/historical?start=...&end=...
```

## Status

* [x] Backend API running
* [ ] GUI in progress