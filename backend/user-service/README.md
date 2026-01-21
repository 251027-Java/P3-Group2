# User Service

User management microservice for the Marketplace application. Provides CRUD operations for user accounts.

## Overview

The User Service handles user account management including creation, retrieval, updates, and deletion. It stores user data in PostgreSQL and registers with Eureka for service discovery.

## Technology Stack

- **Java** with **Spring Boot**
- **Spring Data JPA** (PostgreSQL)
- **Spring Cloud** (Eureka Client)
- **SpringDoc OpenAPI** (API Documentation)


## API Documentation

### Base URL

```
http://localhost:8083/api/users
```

### OpenAPI / Swagger UI

- **Swagger UI**: `http://localhost:8083/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8083/v3/api-docs`

---

## Endpoints

### Create User

Creates a new user account.

**Endpoint:** `POST /api/users`

**Request Body:**

```json
{
  "email": "user@example.com",
  "username": "johndoe",
  "password": "securepassword123",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "role": "USER"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `email` | string | Yes | User's email address |
| `username` | string | Yes | Unique username |
| `password` | string | Yes | User's password |
| `latitude` | number | No | User's latitude coordinate |
| `longitude` | number | No | User's longitude coordinate |
| `role` | string | No | User role (defaults to "USER") |

**Response (201 Created):**

```json
{
  "userId": 1,
  "email": "user@example.com",
  "username": "johndoe",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "role": "USER",
  "createdAt": "2026-01-20T15:30:00"
}
```

**Error Responses:**

| Status | Description |
|--------|-------------|
| `400` | Invalid request or duplicate email/username |

---

### Get User by ID

Retrieves a user by their ID.

**Endpoint:** `GET /api/users/{userId}`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `userId` | number | The user's ID |

**Response (200 OK):**

```json
{
  "userId": 1,
  "email": "user@example.com",
  "username": "johndoe",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "role": "USER",
  "createdAt": "2026-01-20T15:30:00"
}
```

**Error Responses:**

| Status | Description |
|--------|-------------|
| `404` | User not found |

**Example:**

```bash
curl http://localhost:8083/api/users/1
```

---

### Get User by Username

Retrieves a user by their username.

**Endpoint:** `GET /api/users/username/{username}`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `username` | string | The user's username |

**Response (200 OK):**

```json
{
  "userId": 1,
  "email": "user@example.com",
  "username": "johndoe",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "role": "USER",
  "createdAt": "2026-01-20T15:30:00"
}
```

**Error Responses:**

| Status | Description |
|--------|-------------|
| `404` | User not found |

**Example:**

```bash
curl http://localhost:8083/api/users/username/johndoe
```

---

### Get User by Email

Retrieves a user by their email address.

**Endpoint:** `GET /api/users/email/{email}`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `email` | string | The user's email address |

**Response (200 OK):**

```json
{
  "userId": 1,
  "email": "user@example.com",
  "username": "johndoe",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "role": "USER",
  "createdAt": "2026-01-20T15:30:00"
}
```

**Error Responses:**

| Status | Description |
|--------|-------------|
| `404` | User not found |

**Example:**

```bash
curl http://localhost:8083/api/users/email/user@example.com
```

---

### Get All Users

Retrieves all users in the system.

**Endpoint:** `GET /api/users`

**Response (200 OK):**

```json
[
  {
    "userId": 1,
    "email": "user@example.com",
    "username": "johndoe",
    "latitude": 40.7128,
    "longitude": -74.0060,
    "role": "USER",
    "createdAt": "2026-01-20T15:30:00"
  },
  {
    "userId": 2,
    "email": "admin@example.com",
    "username": "admin",
    "latitude": 34.0522,
    "longitude": -118.2437,
    "role": "ADMIN",
    "createdAt": "2026-01-20T14:00:00"
  }
]
```

**Example:**

```bash
curl http://localhost:8083/api/users
```

---

### Update User

Updates an existing user's information.

**Endpoint:** `PUT /api/users/{userId}`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `userId` | number | The user's ID |

**Request Body:**

```json
{
  "email": "newemail@example.com",
  "username": "newusername",
  "latitude": 41.8781,
  "longitude": -87.6298
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `email` | string | No | New email address |
| `username` | string | No | New username |
| `latitude` | number | No | New latitude coordinate |
| `longitude` | number | No | New longitude coordinate |

**Response (200 OK):**

```json
{
  "userId": 1,
  "email": "newemail@example.com",
  "username": "newusername",
  "latitude": 41.8781,
  "longitude": -87.6298,
  "role": "USER",
  "createdAt": "2026-01-20T15:30:00"
}
```

**Error Responses:**

| Status | Description |
|--------|-------------|
| `400` | Invalid request |

**Example:**

```bash
curl -X PUT http://localhost:8083/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newemail@example.com",
    "username": "newusername"
  }'
```

---

### Delete User

Deletes a user from the system.

**Endpoint:** `DELETE /api/users/{userId}`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `userId` | number | The user's ID |

**Response (204 No Content):**

No response body.

**Error Responses:**

| Status | Description |
|--------|-------------|
| `404` | User not found |

**Example:**

```bash
curl -X DELETE http://localhost:8083/api/users/1
```

---

## Data Model

### UserResponse

| Field | Type | Description |
|-------|------|-------------|
| `userId` | Long | Unique user identifier |
| `email` | String | User's email address |
| `username` | String | User's display name |
| `latitude` | Double | User's latitude coordinate |
| `longitude` | Double | User's longitude coordinate |
| `role` | String | User role (USER, ADMIN) |
| `createdAt` | LocalDateTime | Account creation timestamp |

---

## Project Structure

```
user-service/
├── src/main/java/org/example/
│   ├── UserApplication.java        # Main application class
│   ├── Controller/
│   │   └── UserController.java     # REST endpoints
│   ├── dto/
│   │   ├── CreateUserRequest.java  # Create user DTO
│   │   ├── UpdateUserRequest.java  # Update user DTO
│   │   └── UserResponse.java       # Response DTO
│   ├── Model/
│   │   └── User.java               # User entity
│   ├── Repository/
│   │   └── UserRepository.java     # JPA repository
│   └── Service/
│       └── UserService.java        # Business logic
├── src/main/resources/
│   └── application.yaml            # Configuration
└── pom.xml
```
