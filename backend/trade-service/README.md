# Trade Service

## Overview

Trade Service is a Spring Boot microservice that handles trade requests and management for a marketplace web application. It allows users to initiate trade requests for listings and enables listing owners to accept or decline incoming trade requests.

## Features

- **Trade Request Management**: Create, accept, and decline trade requests
- **Service Discovery**: Registered with Eureka for service discovery
- **Service Communication**: Uses OpenFeign for inter-service communication with listing-service and user-service
- **Event-Driven Architecture**: Publishes and consumes events via Kafka
- **Structured Logging**: LogBack with Logstash JSON encoder for centralized logging
- **API Documentation**: OpenAPI/Swagger UI for API documentation
- **Comprehensive Testing**: JUnit 5 tests with 60%+ line coverage

## Technology Stack

- **Spring Boot**: 4.0.1
- **Spring Cloud**: 2025.1.0
- **Spring Data JPA**: For database interactions
- **Hibernate**: ORM framework
- **PostgreSQL**: Database
- **Eureka Client**: Service discovery
- **OpenFeign**: Service-to-service communication
- **Apache Kafka**: Event-driven messaging
- **Lombok**: Code generation
- **LogBack**: Logging framework with Logstash encoder
- **OpenAPI/Swagger**: API documentation
- **JUnit 5 & Mockito**: Testing framework

## Prerequisites

- Java 21
- Maven 3.8+
- PostgreSQL 16+
- Apache Kafka
- Eureka Server (for service discovery)

## Database Schema

The service uses the following tables:
- `trades`: Stores trade requests
- `TradeOfferedCards`: Stores cards offered in each trade

## Configuration

### Application Properties

### Environment Variables

You can override configuration using environment variables:
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `SPRING_KAFKA_BOOTSTRAP_SERVERS`: Kafka bootstrap servers
- `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE`: Eureka server URL

## API Endpoints

### Create Trade Request
```
POST /api/trades
Content-Type: application/json

{
  "listingId": 1,
  "requestingUserId": 2,
  "offeredCardIds": [10, 11]
}
```

### Accept Trade Request
```
PUT /api/trades/{tradeId}/accept?listingOwnerId={ownerId}
```

### Decline Trade Request
```
PUT /api/trades/{tradeId}/decline?listingOwnerId={ownerId}
```

### Get All Trades
```
GET /api/trades
```

### Get Trade by ID
```
GET /api/trades/{tradeId}
```

### Get Trades by Listing ID
```
GET /api/trades/listing/{listingId}
```

### Get Trades by User ID
```
GET /api/trades/user/{userId}
```

## API Documentation

Once the service is running, access the Swagger UI at:
- http://localhost:8083/swagger-ui.html

API documentation (OpenAPI JSON) is available at:
- http://localhost:8083/api-docs

## Running the Service

### Local Development

1. Ensure PostgreSQL is running. The database will be created automatically by the docker-compose setup, or you can create it manually:
   ```sql
   CREATE DATABASE marketplace;
   ```

2. Ensure Kafka is running on localhost:9092

3. Ensure Eureka Server is running on localhost:8761

4. Build and run:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

### Docker

## Testing

### Run Tests
```bash
mvn test
```

### Test Coverage
The project uses JaCoCo for code coverage. Generate coverage report:
```bash
mvn clean test jacoco:report
```

Coverage report will be available at: `target/site/jacoco/index.html`

### Test Coverage Target
- Minimum 60% line coverage
- Tests use JUnit 5 and Mockito for mocking

## Kafka Events

### Published Events

The service publishes the following events to the `trade-events` topic:

- **TRADE_CREATED**: When a new trade request is created
- **TRADE_ACCEPTED**: When a trade request is accepted
- **TRADE_DECLINED**: When a trade request is declined

Event structure:
```json
{
  "eventType": "TRADE_CREATED",
  "tradeId": 1,
  "listingId": 1,
  "requestingUserId": 2,
  "timestamp": 1234567890
}
```

### Consumed Events

The service consumes events from:
- `listing-events`: For handling listing-related events
- `user-events`: For handling user-related events

## Logging

The service uses LogBack with Logstash JSON encoder for structured logging. Logs are:
- Output to console in development
- Written to `logs/trade-service.log` in production
- Sent to Logstash (if configured) on port 5000

Log levels:
- Root: INFO
- Service package: DEBUG

## Service Dependencies

This service depends on:
- **listing-service**: For fetching listing information and updating listing status
- **user-service**: For validating user existence

Ensure these services are registered with Eureka and accessible.

## Error Handling

The service includes comprehensive error handling:
- `ResourceNotFoundException`: For missing resources (404)
- `TradeException`: For business logic errors (400)
- `GlobalExceptionHandler`: Centralized exception handling

## Project Structure

```
trade-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/marketplace/trade/
│   │   │       ├── TradeServiceApplication.java
│   │   │       ├── config/
│   │   │       ├── consumer/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── exception/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       └── client/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── logback-spring.xml
│   └── test/
│       └── java/
│           └── com/pokemon/trading/tradeservice/
│               ├── controller/
│               └── service/
├── pom.xml
├── Dockerfile
└── README.md
```

## Contributing

When contributing to this project:
1. Follow the existing code structure and patterns
2. Ensure all tests pass
3. Maintain 60%+ test coverage
4. Update API documentation as needed
5. Add appropriate logging

## License

[Add your license information here]

## Notes

- **Spring Boot Version**: The project uses Spring Boot 4.0.1 with Spring Cloud 2025.1.0 (Oakwood release train).
- **Database**: The service uses PostgreSQL 16+. The database schema is automatically initialized when using docker-compose, or you can run the SQL scripts from the `database/` directory to set up the schema manually.
- **Service Discovery**: The service must be able to reach the Eureka server for service discovery to work properly.
