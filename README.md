# Energy Communities

This project implements a distributed system for simulating an energy community. Energy production and consumption messages are sent through RabbitMQ, processed by backend services, stored in PostgreSQL, exposed through a Spring Boot REST API, and visualized in a JavaFX desktop client.

## Components

The final system consists of six independently startable applications:

* **community-producer:** Sends community energy production messages to RabbitMQ.
* **community-user:** Sends community energy usage messages to RabbitMQ.
* **usage-service:** Consumes production and usage messages, updates the hourly usage table, and publishes usage update messages.
* **percentage-service:** Consumes usage update messages and updates the current percentage table.
* **backend-api:** Spring Boot REST API that reads current and historical energy data from PostgreSQL.
* **gui:** JavaFX desktop client that displays current percentages and historical energy usage.

Supporting infrastructure:

* **PostgreSQL:** Stores hourly usage data and calculated percentage data.
* **RabbitMQ:** Message broker for asynchronous communication between services.

## Architecture Flow

1. The community producer sends `PRODUCER` messages to RabbitMQ.
2. The community user sends `USER` messages to RabbitMQ.
3. The usage service consumes messages from `energy.messages`.
4. The usage service updates the `energy_usage` table.
5. The usage service publishes an update message to `usage.updates`.
6. The percentage service consumes the update message.
7. The percentage service updates the `current_percentage` table.
8. The backend API reads the database tables.
9. The JavaFX GUI fetches current and historical data from the REST API.

## Requirements

Make sure the following tools are installed:

* Java 21
* Maven or the included Maven Wrapper
* Docker

## Infrastructure

Start PostgreSQL and RabbitMQ from the project root:

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

To reset the database and reload the seed data:

```bash
docker compose down -v
docker compose up -d
```

## Shared Contracts

### RabbitMQ Queues

```text
energy.messages
usage.updates
```

### Energy Message

The producer and user applications send messages to `energy.messages`.

Producer message:

```json
{
  "type": "PRODUCER",
  "association": "COMMUNITY",
  "kwh": 0.025,
  "datetime": "2025-01-10T14:33:00"
}
```

User message:

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

Stores calculated percentage values.

```text
hour
community_depleted
grid_portion
updated_at
```

The table can contain multiple hourly rows. The REST API and GUI use the newest hour for the current percentage display.

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
    "hour": "2025-01-10T14:00:00",
    "communityProduced": 143.024,
    "communityUsed": 130.101,
    "gridUsed": 14.75
  },
  {
    "hour": "2025-01-10T13:00:00",
    "communityProduced": 120.5,
    "communityUsed": 110.3,
    "gridUsed": 10.2
  }
]
```

## Producer Logic

The community producer sends messages in random intervals between 1 and 5 seconds.

The produced kWh value is influenced by:

* Time of day
* Daylight factor
* Weather data from Open-Meteo

The Open-Meteo request uses Vienna, Austria as demo location:

```text
Latitude: 48.2082
Longitude: 16.3738
```

At night, the producer can send `0.0000 kWh`, because the simulation assumes no solar production without daylight.

## User Logic

The community user sends messages in random intervals between 1 and 5 seconds.

The used kWh value is influenced by the time of day:

* Morning peak: higher consumption
* Evening peak: higher consumption
* Night: lower consumption
* Other times: normal consumption

## Usage Logic

The usage service processes messages in the order in which they are received.

For `USER` messages, available community energy is used first. If the requested usage exceeds the available community energy pool, the remaining amount is assigned to grid usage.

The implementation follows a first-come-first-serve approach and does not rebalance already processed usage retroactively.

## Percentage Logic

The percentage service receives usage update messages from the usage service.

It calculates:

```text
community_depleted = community_used / community_produced * 100
grid_portion = grid_used / (community_used + grid_used) * 100
```

If no community energy is available and user consumption is assigned fully to the grid, the GUI can show:

```text
Community Pool Usage: 0.00%
Grid Portion: 100.00%
```

This is expected behavior.

## Start Order

Recommended start order:

### 1. Infrastructure

From the project root:

```bash
docker compose up -d
```

### 2. Usage Service

```bash
cd usage-service
./mvnw spring-boot:run
```

Windows:

```powershell
cd usage-service
.\mvnw.cmd spring-boot:run
```

### 3. Percentage Service

```bash
cd percentage-service
./mvnw spring-boot:run
```

Windows:

```powershell
cd percentage-service
.\mvnw.cmd spring-boot:run
```

### 4. Backend API

```bash
cd backend-api
./mvnw spring-boot:run
```

Windows:

```powershell
cd backend-api
.\mvnw.cmd spring-boot:run
```

### 5. Community Producer

```bash
cd community-producer
./mvnw spring-boot:run
```

Windows:

```powershell
cd community-producer
.\mvnw.cmd spring-boot:run
```

### 6. Community User

```bash
cd community-user
./mvnw spring-boot:run
```

Windows:

```powershell
cd community-user
.\mvnw.cmd spring-boot:run
```

### 7. GUI

```bash
cd gui
./mvnw javafx:run
```

Windows:

```powershell
cd gui
.\mvnw.cmd javafx:run
```

## Final Smoke Test

### 1. Check Docker containers

```bash
docker compose ps
```

Expected:

```text
energy-postgres
energy-rabbitmq
```

### 2. Check RabbitMQ queues

Open:

```text
http://localhost:15672
```

Expected queues:

```text
energy.messages
usage.updates
```

During a running system, messages should not permanently pile up. Usually `Ready` and `Unacked` should return to `0`.

### 3. Check database tables

```bash
docker exec -it energy-postgres psql -U disysuser -d energy_communities
```

Then run:

```sql
SELECT * FROM energy_usage ORDER BY hour DESC;
SELECT * FROM current_percentage ORDER BY hour DESC;
```

Expected:

* `energy_usage` contains hourly produced, used and grid values.
* `current_percentage` contains calculated percentage values.

Exit psql:

```sql
\q
```

### 4. Check REST API

```bash
curl -i http://localhost:8080/energy/current
```

```bash
curl -i "http://localhost:8080/energy/historical?start=2025-01-09T00:00:00&end=2025-01-10T23:59:59"
```

Expected:

* HTTP 200
* JSON response
* Current endpoint returns the latest current values
* Historical endpoint returns rows for the selected time period

### 5. Check GUI

In the JavaFX GUI:

1. Click **Refresh**.
2. Check the current community pool usage and grid portion.
3. Select a historical date range.
4. Click **Fetch**.
5. Check produced, used and grid used values in kWh.

## Authors

* Festus Ibitoye
* Albina Strecker