# Energy Communities

This project implements a distributed system for simulating an energy community. Energy production and consumption messages are sent through RabbitMQ, processed by backend services, stored in PostgreSQL, exposed through a Spring Boot REST API, and visualized in a JavaFX desktop client.

## Components

The final system consists of six independently startable applications:

* **community-producer:** Sends community energy production messages to RabbitMQ.
* **community-user:** Sends community energy usage messages to RabbitMQ.
* **usage-service:** Consumes production and usage messages, updates the hourly usage table, and publishes usage update events.
* **percentage-service:** Consumes usage update events and updates the current percentage table.
* **backend-api:** Spring Boot REST API that reads current and historical energy data from PostgreSQL.
* **gui:** JavaFX desktop client that displays current percentages and historical energy usage.

Supporting infrastructure:

* **PostgreSQL:** Stores usage and percentage data.
* **RabbitMQ:** Message broker for asynchronous service communication.

## Requirements

Make sure the following tools are installed:

* Java 21
* Maven or the included Maven Wrapper
* Docker

## Infrastructure

Start PostgreSQL and RabbitMQ:

```bash
docker compose up -d
```

This starts:

* PostgreSQL on port `5432`
* RabbitMQ on port `5672`
* RabbitMQ Management UI on port `15672`

RabbitMQ Management UI:

```text
http://localhost:15672
```

Default credentials:

```text
Username: disysuser
Password: disyspw
```

## Shared Contracts

### RabbitMQ Queues

```text
energy.messages
usage.updates
```

### Energy Message

Producer and user applications send messages to `energy.messages`.

```json
{
  "type": "PRODUCER",
  "association": "COMMUNITY",
  "kwh": 0.025,
  "datetime": "2025-01-10T14:33:00"
}
```

For usage messages, `type` is `USER`.

```json
{
  "type": "USER",
  "association": "COMMUNITY",
  "kwh": 0.018,
  "datetime": "2025-01-10T14:34:00"
}
```

### Usage Update Message

The usage service sends update messages to `usage.updates`.

```json
{
  "hour": "2025-01-10T14:00:00",
  "communityProduced": 143.024,
  "communityUsed": 130.101,
  "gridUsed": 14.75
}
```

## Database Tables

### energy_usage

Stores hourly energy values in kWh.

```text
hour
community_produced
community_used
grid_used
updated_at
```

### current_percentage

Stores the calculated current percentages.

```text
hour
community_depleted
grid_portion
updated_at
```

## REST API

The JavaFX GUI uses the following REST endpoints:

```text
GET /energy/current
GET /energy/historical?start=2025-01-10T00:00:00&end=2025-01-10T23:59:59
```

### Current Energy Response

```json
{
  "hour": "2025-01-10T14:00:00",
  "communityProduced": 143.024,
  "communityUsed": 130.101,
  "gridUsed": 14.75,
  "communityDepleted": 90.97,
  "gridPortion": 10.18
}
```

### Historical Energy Response

```json
[
  {
    "hour": "2025-01-10T13:00:00",
    "communityProduced": 120.5,
    "communityUsed": 110.3,
    "gridUsed": 10.2
  },
  {
    "hour": "2025-01-10T14:00:00",
    "communityProduced": 143.024,
    "communityUsed": 130.101,
    "gridUsed": 14.75
  }
]
```

## Start Order

Recommended start order:

1. Infrastructure

```bash
docker compose up -d
```

2. Usage Service

```bash
cd usage-service
./mvnw spring-boot:run
```

3. Percentage Service

```bash
cd percentage-service
./mvnw spring-boot:run
```

4. Backend API

```bash
cd backend-api
./mvnw spring-boot:run
```

5. Producer

```bash
cd community-producer
./mvnw spring-boot:run
```

6. User

```bash
cd community-user
./mvnw spring-boot:run
```

7. GUI

```bash
cd gui
./mvnw javafx:run
```

On Windows, use:

```powershell
.\mvnw.cmd spring-boot:run
```

or:

```powershell
.\mvnw.cmd javafx:run
```

## Authors

* Festus Ibitoye - [@fesit](https://github.com/fesit)
* Albina Strecker - [@flyverseHQ](https://github.com/flyverseHQ)