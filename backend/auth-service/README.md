# Auth Service

Authentication microservice for the Marketplace application. Provides user registration, login, and JWT token generation.

## Overview

The Auth Service handles user authentication and authorization using JWT (JSON Web Tokens). It's designed to work as part of a microservices architecture, registering with Eureka for service discovery.

## Technology Stack

- **Java 25**
- **Spring Boot 4.0.1**
- **Spring Cloud** (Eureka Client)
- **JJWT** (JWT implementation)
- **Lombok** (Boilerplate reduction)
- **SpringDoc OpenAPI** (API Documentation)

## Getting Started

### Prerequisites

- Java 25+
- Maven 3.8+
- Eureka Server running (optional, for service discovery)

### Configuration

The service can be configured via environment variables or `application.yaml`:

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8082` | Server port |
| `JWT_SECRET` | `your-256-bit-secret-key-here-change-in-production` | JWT signing secret (min 256-bit) |
| `JWT_EXPIRATION` | `86400000` | Token expiration in milliseconds (24 hrs) |
| `EUREKA_URL` | `http://localhost:8761/eureka` | Eureka server URL |

### Running the Service

```bash
# Navigate to auth-service directory
cd backend/auth-service

# Run with Maven
./mvnw spring-boot:run

# Or build and run JAR
./mvnw clean package
java -jar target/auth-0.0.1-SNAPSHOT.jar
```

### Running Tests

```bash
./mvnw test
```

---

## API Documentation

### Base URL

```
http://localhost:8082/api/auth
```

### OpenAPI / Swagger UI

When the service is running, visit:
- **Swagger UI**: `http://localhost:8082/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8082/v3/api-docs`

---

## Endpoints

### Register User

Creates a new user account and returns a JWT token.

**Endpoint:** `POST /api/auth/register`

**Request Body:**

```json
{
  "email": "user@example.com",
  "username": "johndoe",
  "password": "securepassword123"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `email` | string | Yes | User's email address (must be unique) |
| `username` | string | Yes | Display name for the user |
| `password` | string | Yes | User's password |

**Response (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400000,
  "username": "johndoe",
  "role": "USER"
}
```

| Field | Type | Description |
|-------|------|-------------|
| `token` | string | JWT access token |
| `type` | string | Token type (always "Bearer") |
| `expiresIn` | number | Token expiration time in milliseconds |
| `username` | string | The registered username |
| `role` | string | User role (USER or ADMIN) |

**Error Responses:**

| Status | Description |
|--------|-------------|
| `400` | Email already registered |
| `500` | Internal server error |

**Example:**

```bash
curl -X POST http://localhost:8082/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@example.com",
    "username": "newuser",
    "password": "password123"
  }'
```

---

### Login

Authenticates a user and returns a JWT token.

**Endpoint:** `POST /api/auth/login`

**Request Body:**

```json
{
  "email": "user@example.com",
  "password": "securepassword123"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `email` | string | Yes | User's email address |
| `password` | string | Yes | User's password |

**Response (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400000,
  "username": "johndoe",
  "role": "USER"
}
```

| Field | Type | Description |
|-------|------|-------------|
| `token` | string | JWT access token |
| `type` | string | Token type (always "Bearer") |
| `expiresIn` | number | Token expiration time in milliseconds |
| `username` | string | The authenticated username |
| `role` | string | User role (USER or ADMIN) |

**Error Responses:**

| Status | Description |
|--------|-------------|
| `400` | Invalid email or password |
| `401` | Unauthorized |
| `500` | Internal server error |

**Example:**

```bash
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "admin123"
  }'
```

---

## Seeded Users

For development and testing, the following users are pre-seeded:

| Email | Username | Password | Role |
|-------|----------|----------|------|
| `admin@example.com` | admin | admin123 | ADMIN |
| `user@example.com` | user | user123 | USER |

---

## JWT Token Structure

The generated JWT tokens contain the following claims:

| Claim | Description |
|-------|-------------|
| `sub` | Subject (username) |
| `role` | User role (USER or ADMIN) |
| `iat` | Issued at timestamp |
| `exp` | Expiration timestamp |

### Using the Token

Include the token in the `Authorization` header for authenticated requests:

```bash
curl -X GET http://localhost:8080/api/protected-resource \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## Project Structure

```
auth-service/
├── src/
│   ├── main/
│   │   ├── java/com/marketplace/auth/
│   │   │   ├── AuthServiceApplication.java    # Main application class
│   │   │   ├── config/
│   │   │   │   └── AuthConfig.java            # Bean configuration
│   │   │   ├── controller/
│   │   │   │   └── AuthController.java        # REST endpoints
│   │   │   ├── dto/
│   │   │   │   ├── AuthResponse.java          # Response DTO
│   │   │   │   ├── LoginRequest.java          # Login request DTO
│   │   │   │   └── RegisterRequest.java       # Registration request DTO
│   │   │   ├── model/
│   │   │   │   ├── AppUser.java               # User entity
│   │   │   │   └── Role.java                  # Role enum
│   │   │   └── service/
│   │   │       ├── AuthService.java           # Authentication logic
│   │   │       └── JwtUtil.java               # JWT utilities
│   │   └── resources/
│   │       └── application.yaml               # Configuration
│   └── test/
│       ├── java/com/marketplace/auth/
│       │   ├── controller/
│       │   │   └── AuthControllerTest.java    # Controller tests
│       │   └── service/
│       │       ├── AuthServiceTest.java       # Service tests
│       │       └── JwtUtilTest.java           # JWT utility tests
│       └── resources/
│           └── application.yaml               # Test configuration
└── pom.xml
```

---

## Testing

The auth-service includes comprehensive unit tests:

- **JwtUtilTest** - Tests for JWT token generation, extraction, and validation
- **AuthServiceTest** - Tests for user registration and login logic with mocked dependencies

Run all tests:
```bash
./mvnw test
```

---

## AI Contribution

Files containing AI-generated code are marked with a header:
```java
// Generated with assistance from Antigravity through Gemini
// Reviewed and modified by Liam Ruiz
```


