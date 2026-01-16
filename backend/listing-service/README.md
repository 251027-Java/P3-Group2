# Listing Service

A Spring Boot microservice for managing card trade listings in a Facebook Marketplace-style application. This service allows users to create, update, and manage their card listings for trading with other users.

## Table of Contents

- [Overview](#overview)
- [Technologies](#technologies)
- [Architecture](#architecture)
- [API Endpoints](#api-endpoints)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Docker](#docker)
- [Testing](#testing)
- [Kafka Events](#kafka-events)
- [API Documentation](#api-documentation)

## Overview

The Listing Service is part of a microservices architecture for a card trading marketplace. It provides:

- CRUD operations for card trade listings
- Integration with Eureka for service discovery
- Kafka event publishing for event-driven architecture
- OpenFeign clients for service-to-service communication
- Comprehensive API documentation via Swagger/OpenAPI

## Technologies

- **Java 21** - Programming language
- **Spring Boot 3.4.0** - Application framework
- **Spring Cloud 2024.0.0** - Microservices support
  - Netflix Eureka Client - Service discovery
  - OpenFeign - Declarative REST clients
- **Spring Data JPA** - Data persistence with Hibernate ORM
- **Spring Kafka** - Event-driven messaging
- **PostgreSQL** - Primary database
- **H2** - In-memory database for testing
- **Lombok** - Boilerplate code reduction
- **Logback** - Logging framework
- **SpringDoc OpenAPI** - API documentation
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework
- **JaCoCo** - Code coverage (60% minimum line coverage)
- **Docker** - Containerization

## Architecture

```
listing-service/
├── src/
│   ├── main/
│   │   ├── java/com/marketplace/listingservice/
│   │   │   ├── client/              # Feign clients for external services
│   │   │   │   ├── dto/             # DTOs for client responses
│   │   │   │   ├── CardServiceClient.java
│   │   │   │   ├── CardServiceClientFallback.java
│   │   │   │   ├── UserServiceClient.java
│   │   │   │   └── UserServiceClientFallback.java
│   │   │   ├── config/              # Configuration classes
│   │   │   │   ├── KafkaConfig.java
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── controller/          # REST controllers
│   │   │   │   └── ListingController.java
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── CreateListingRequest.java
│   │   │   │   ├── UpdateListingRequest.java
│   │   │   │   ├── ListingResponse.java
│   │   │   │   └── ErrorResponse.java
│   │   │   ├── entity/              # JPA entities
│   │   │   │   ├── Listing.java
│   │   │   │   └── ListingStatus.java
│   │   │   ├── exception/           # Exception handling
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ListingNotFoundException.java
│   │   │   │   ├── InvalidListingOperationException.java
│   │   │   │   └── CardNotFoundException.java
│   │   │   ├── kafka/               # Kafka producers/consumers
│   │   │   │   ├── ListingEvent.java
│   │   │   │   ├── ListingEventProducer.java
│   │   │   │   └── ListingEventConsumer.java
│   │   │   ├── repository/          # Data repositories
│   │   │   │   └── ListingRepository.java
│   │   │   ├── service/             # Business logic
│   │   │   │   ├── ListingService.java
│   │   │   │   └── impl/
│   │   │   │       └── ListingServiceImpl.java
│   │   │   └── ListingServiceApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-test.yml
│   │       └── logback-spring.xml
│   └── test/                        # Unit and integration tests
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── pom.xml
└── README.md
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/listings` | Create a new listing |
| GET | `/api/listings` | Get all listings |
| GET | `/api/listings/{id}` | Get listing by ID |
| GET | `/api/listings/active` | Get all active listings |
| GET | `/api/listings/user/{userId}` | Get listings by owner |
| GET | `/api/listings/card/{cardId}` | Get listings by card |
| GET | `/api/listings/status/{status}` | Get listings by status |
| PUT | `/api/listings/{id}` | Update a listing |
| POST | `/api/listings/{id}/cancel` | Cancel a listing |
| POST | `/api/listings/{id}/complete` | Complete a listing |
| DELETE | `/api/listings/{id}` | Delete a listing |

### Request/Response Examples

#### Create Listing

**Request:**
```json
POST /api/listings
{
  "ownerUserId": 1,
  "cardId": 100,
  "conditionRating": 8
}
```

**Response:**
```json
{
  "listingId": 1,
  "ownerUserId": 1,
  "cardId": 100,
  "conditionRating": 8,
  "listingStatus": "ACTIVE",
  "createdAt": "2026-01-12T10:30:00"
}
```

#### Update Listing

**Request:**
```json
PUT /api/listings/1
{
  "conditionRating": 9,
  "listingStatus": "ACTIVE"
}
```

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.9+
- PostgreSQL 14+
- Kafka (optional, for event-driven features)
- Eureka Server (optional, for service discovery)

### Local Development

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd backend/listing-service
   ```

2. **Configure the database:**
   
   Update `application.yml` with your PostgreSQL credentials or set environment variables:
   ```bash
   export DB_HOST=localhost
   export DB_PORT=5432
   export DB_NAME=marketplace
   export DB_USERNAME=postgres
   export DB_PASSWORD=password
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the API:**
   - Application: http://localhost:8081
   - Swagger UI: http://localhost:8081/swagger-ui.html
   - OpenAPI Docs: http://localhost:8081/api-docs

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Application port | 8081 |
| `DB_HOST` | MySQL host | localhost |
| `DB_PORT` | PostgreSQL port | 5432 |
| `DB_NAME` | Database name | marketplace |
| `DB_USERNAME` | Database username | postgres |
| `DB_PASSWORD` | Database password | password |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka servers | localhost:9092 |
| `EUREKA_URI` | Eureka server URL | http://localhost:8761/eureka |

### Logging Configuration

Logging is configured using Logback with the following log levels:
- `DEBUG` - Application code (`com.marketplace.listingservice`)
- `INFO` - Spring Framework, Kafka
- `WARN` - Hibernate, Eureka

Logs are written to both console and file (`logs/listing-service.log`).

## Docker

### Build and Run with Docker

```bash
# Build the Docker image
docker build -t listing-service:latest .

# Run with Docker Compose (includes MySQL and Kafka)
docker-compose up -d
```

### Docker Compose Services

- **listing-service** - The main application (port 8081)
- **postgres** - PostgreSQL database (port 5432)
- **zookeeper** - Kafka dependency
- **kafka** - Kafka message broker (port 9092)

## Testing

### Run Tests

```bash
# Run all tests
mvn test

# Run tests with coverage report
mvn test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Test Coverage Requirements

- Minimum **60% line coverage** enforced by JaCoCo
- Unit tests use JUnit 5 and Mockito
- Repository tests use H2 in-memory database
- Controller tests use MockMvc

### Test Structure

- `ListingServiceImplTest` - Service layer unit tests
- `ListingControllerTest` - Controller layer unit tests
- `ListingRepositoryTest` - Repository integration tests
- `ListingEntityTest` - Entity and enum tests
- `DtoTest` - DTO tests

## Kafka Events

The service publishes the following events:

| Topic | Event Type | Description |
|-------|------------|-------------|
| `listing-created` | LISTING_CREATED | New listing created |
| `listing-updated` | LISTING_UPDATED | Listing details updated |
| `listing-status-changed` | LISTING_STATUS_CHANGED | Listing status changed |
| `listing-deleted` | LISTING_DELETED | Listing deleted |

### Event Payload Example

```json
{
  "eventType": "LISTING_CREATED",
  "listingId": 1,
  "ownerUserId": 100,
  "cardId": 200,
  "conditionRating": 8,
  "listingStatus": "active",
  "timestamp": "2026-01-12T10:30:00",
  "additionalInfo": null
}
```

### Consumed Events

The service listens for:
- `card-deleted` - Handle card deletion
- `user-deleted` - Handle user deletion  
- `trade-completed` - Mark listing as completed

## API Documentation

Interactive API documentation is available via Swagger UI:

- **Swagger UI:** http://localhost:8081/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8081/api-docs
- **OpenAPI YAML:** http://localhost:8081/api-docs.yaml

## Database Schema

```sql
CREATE TABLE listings (
    listingId INT PRIMARY KEY AUTO_INCREMENT,
    ownerUserId INT NOT NULL,
    cardId INT NOT NULL,
    condition_rating INT CHECK (condition_rating BETWEEN 1 AND 10),
    listingStatus VARCHAR(50) DEFAULT 'active',
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ownerUserId) REFERENCES appUser(appUserId),
    FOREIGN KEY (cardId) REFERENCES cards(cardId)
);
```

## Health Check

The service exposes health endpoints via Spring Actuator:

- **Health:** http://localhost:8081/actuator/health
- **Info:** http://localhost:8081/actuator/info
- **Metrics:** http://localhost:8081/actuator/metrics

## Contributing

1. Follow the existing code style and patterns
2. Write unit tests for new features
3. Maintain minimum 60% code coverage
4. Update documentation as needed

## License

This project is part of the Revature P3 Group 2 project.