# Energy Communities

This project implements a distributed system for simulating an energy community, where energy production and consumption are processed and visualized.

The system follows a modular architecture with a REST API backend, a JavaFX desktop client, and supporting infrastructure using Docker.

## Components

* **backend-api:** Spring Boot REST API
* **gui:** JavaFX client
* **PostgreSQL:** (Docker)
* **RabbitMQ:** (Docker, prepared for later stages)

---

## Requirements

Make sure the following tools are installed:

* **Java 21** (e.g., Zulu / OpenJDK)
* **Maven** (or use the Maven Wrapper included in the project)
* **Docker**

---

## Setup Instructions

### 1. Start Infrastructure

Start the required database and message broker using Docker:
```bash
docker compose up -d
```

This will:
* Start PostgreSQL
* Automatically create the database
* Create tables
* Insert sample data

### 2. Run Backend

**Using IntelliJ (recommended):**
1. Open the project
2. Run `BackendApiApplication`

**Or via terminal:**
```powershell
cd backend-api
.\mvnw.cmd spring-boot:run
```

*The backend will run on: http://localhost:8080/*

### 3. Start GUI

**Using IntelliJ:**
1. Open `EnergyGuiApplication.java`
2. Right-click → Run

**Or via terminal:**
```powershell
cd gui
.\mvnw.cmd javafx:run
```

*The current dashboard will pop up: You can see the current usage via mouse click and also display some historical data.*

---

## Authors

* **Festus Ibitoye** - [@fesit](https://github.com/fesit)
* **Albina Strecker** - [@flyverseHQ](https://github.com/flyverseHQ)