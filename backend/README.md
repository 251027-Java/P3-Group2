# Backend Services

This directory contains all microservices for the Marketplace application.

## Architecture Overview

The backend follows a microservices architecture with the following components:

| Service | Port | Description |
|---------|------|-------------|
| **API Gateway** | 8080 | Entry point, routes requests, JWT validation |
| **Eureka Server** | 8761 | Service discovery |
| **Auth Service** | 8082 | Authentication & JWT token generation |
| **User Service** | 8083 | User account management |
| **Card Service** | 8084 | Trading card data management |
| **Listing Service** | 8081 | Card trade listings |
| **Trade Service** | 8085 | Trade request management |

---

## API Endpoints

All endpoints are accessible through the API Gateway at `http://localhost:8080`.

---

### Auth Service (`/api/auth`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Authenticate and get JWT token |

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "username": "johndoe",
  "password": "securepassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400000,
  "username": "johndoe",
  "role": "USER"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securepassword123"
}
```

**Response:** Same as Register

**Seeded Users for Testing:**
| Email | Password | Role |
|-------|----------|------|
| `admin@example.com` | admin123 | ADMIN |
| `user@example.com` | user123 | USER |

---

### User Service (`/api/users`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users` | Create a new user |
| GET | `/api/users` | Get all users |
| GET | `/api/users/{userId}` | Get user by ID |
| GET | `/api/users/username/{username}` | Get user by username |
| GET | `/api/users/email/{email}` | Get user by email |
| PUT | `/api/users/{userId}` | Update user |
| DELETE | `/api/users/{userId}` | Delete user |

#### Create User
```http
POST /api/users
Content-Type: application/json

{
  "email": "user@example.com",
  "username": "johndoe",
  "password": "securepassword123",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "role": "USER"
}
```

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

#### Update User
```http
PUT /api/users/{userId}
Content-Type: application/json

{
  "email": "newemail@example.com",
  "username": "newusername",
  "latitude": 41.8781,
  "longitude": -87.6298
}
```

---

### Card Service (`/api/cards`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/cards/sync/{categoryId}/{setId}` | Sync card data from TCGCSV API |
| GET | `/api/cards` | Get all cards |
| GET | `/api/cards?name={name}` | Search cards by name |
| GET | `/api/cards/{cardId}` | Get card by ID |

#### Sync Card Data
```http
POST /api/cards/sync/3/24325
```
- `categoryId`: 3 (Pokemon)
- `setId`: e.g., 24325 (Scarlet & Violet: Black Bolt)

#### Search Cards
```http
GET /api/cards?name=Pikachu
```

---

### Listing Service (`/api/listings`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/listings` | Create a new listing |
| GET | `/api/listings` | Get all listings |
| GET | `/api/listings/{listingId}` | Get listing by ID |
| GET | `/api/listings/active` | Get all active listings |
| GET | `/api/listings/user/{userId}` | Get listings by owner |
| GET | `/api/listings/card/{cardId}` | Get listings by card |
| GET | `/api/listings/status/{status}` | Get listings by status |
| PUT | `/api/listings/{listingId}` | Update a listing |
| POST | `/api/listings/{listingId}/cancel` | Cancel a listing |
| POST | `/api/listings/{listingId}/complete` | Complete a listing |
| DELETE | `/api/listings/{listingId}` | Delete a listing |

#### Create Listing
```http
POST /api/listings
Content-Type: application/json

{
  "ownerUserId": 1,
  "cardId": 100,
  "conditionRating": 8
}
```

**Response (201 Created):**
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
```http
PUT /api/listings/{listingId}
Content-Type: application/json

{
  "conditionRating": 9,
  "listingStatus": "ACTIVE"
}
```

**Listing Statuses:** `ACTIVE`, `COMPLETED`, `CANCELLED`

---

### Trade Service (`/api/trades`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/trades` | Create a trade request |
| GET | `/api/trades` | Get all trades |
| GET | `/api/trades/{tradeId}` | Get trade by ID |
| GET | `/api/trades/listing/{listingId}` | Get trades by listing |
| GET | `/api/trades/user/{userId}` | Get trades by user |
| PUT | `/api/trades/{tradeId}/accept?listingOwnerId={ownerId}` | Accept trade |
| PUT | `/api/trades/{tradeId}/decline?listingOwnerId={ownerId}` | Decline trade |

#### Create Trade Request
```http
POST /api/trades
Content-Type: application/json

{
  "listingId": 1,
  "requestingUserId": 2,
  "offeredCardIds": [10, 11]
}
```

#### Accept Trade
```http
PUT /api/trades/1/accept?listingOwnerId=1
```

#### Decline Trade
```http
PUT /api/trades/1/decline?listingOwnerId=1
```

---

## Authentication

All endpoints (except `/api/auth/**`) require JWT authentication.

Include the token in the `Authorization` header:
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Running the Services

### Using Docker Compose (Recommended)
```bash
docker-compose up
```

### Running Individually
```bash
# Start Eureka Server first
cd eureka-server && ./mvnw spring-boot:run

# Then start other services
cd auth-service && ./mvnw spring-boot:run
cd user-service && ./mvnw spring-boot:run
cd card-service && ./mvnw spring-boot:run
cd listing-service && ./mvnw spring-boot:run
cd trade-service && ./mvnw spring-boot:run

# Start API Gateway last
cd api-gateway && ./mvnw spring-boot:run
```

---

## Swagger Documentation

Each service provides Swagger UI for interactive API documentation:

| Service | Swagger UI URL |
|---------|----------------|
| Auth Service | http://localhost:8082/swagger-ui.html |
| User Service | http://localhost:8083/swagger-ui.html |
| Listing Service | http://localhost:8081/swagger-ui.html |
| Trade Service | http://localhost:8085/swagger-ui.html |

---

## Environment Variables

See `.env.example` in the root directory for required environment variables.

Key variables:
- `JWT_SECRET` - JWT signing secret (min 256-bit)
- `JWT_EXPIRATION` - Token expiration in milliseconds
- `DB_*` - Database connection settings
- `KAFKA_BOOTSTRAP_SERVERS` - Kafka broker address
- `EUREKA_URI` - Eureka server URL

---

## Testing

Run tests for all services:
```bash
./mvnw test
```

Individual service tests:
```bash
cd <service-directory>
./mvnw test
```