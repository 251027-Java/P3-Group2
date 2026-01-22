# P3-Group2

## Table of Contents

- [Overview](#overview)
- [Quick Start](#quick-start)
- [Architecture](#architecture)
- [Services Documentation](#services-documentation)
- [API Reference](#api-reference)
- [Development](#development)
- [Testing](#testing)
- [Deployment](#deployment)

## Overview

This project is a marketplace application for the Pokemon trading card game (TCG). The platform allows users to:

- Browse and search card collections from TCGCSV/TCGPlayer
- Create and manage card trade listings
- Manage user profiles and authentication
- View real-time marketplace updates through event-driven architecture

The application uses a **microservices architecture** on the backend with **Spring Boot** and a **micro-frontend architecture** using **Single-SPA** to orchestrate Angular and React applications.

## Quick Start

This project uses Docker Compose for easy setup of the backend services (eureka server, card, listing, user), frontend services (Angular & React MFEs, root-config), and database configuration.

### Prerequisites
- Docker & Docker Compose
- Java 25+ (for local development)

### Setup

1. **Configure Environment Variables**
   Copy the example environment file to `.env`:
   ```bash
   cp .env.example .env
   ```
   *Note: You may adjust the values in `.env` if you need to perform custom configuration, but the defaults should work out of the box.*

2. **Start Services**
   Run the following command in the root directory:
   ```bash
   docker-compose up --build
   ```

   This will start:
   - PostgreSQL database
   - Eureka discovery server
   - Card Service
   - Listing Service
   - User Service
   - Frontend MFEs

3. **Verify Services**

   Check that all services are running:
   
   **Backend:**
   - Card Service: `http://localhost:8081`
   - Eureka Dashboard: `http://localhost:8761`
   - Card Service Swagger: `http://localhost:8081/swagger-ui/index.html`
   - Listing Service Swagger: `http://localhost:8082/swagger-ui/index.html`
   - PostgreSQL database: Port `5432` (`marketp_db` mapped locally as per `.env`)
   
   **Frontend:**
   - Main Application: `http://localhost:9000`
   - Angular Cards MFE: `http://localhost:4201`
   - React Users MFE: `http://localhost:4202`

4. **Seed the Database**
   
   To populate card data from TCGCSV:
   ```bash
   curl -X POST "http://localhost:8081/api/cards/sync/3/24325"
   ```
   
   Where:
   - `3` = Pokemon category ID
   - `24325` = Scarlet & Violet: Black Bolt set ID

## Architecture

### Microservices Communication

- **Service Discovery**: Eureka Server enables dynamic service registration and discovery
- **Inter-Service Communication**: OpenFeign clients for synchronous REST calls
- **Event-Driven Architecture**: Kafka for asynchronous event publishing/consuming
- **API Gateway**: Single entry point for frontend requests

## Tech Stack

### Backend Services

- **Java 25** - Programming language
- **Spring Boot** - Application framework
- **Spring Cloud** - Microservices infrastructure
  - Eureka - Service discovery
  - OpenFeign - Declarative REST clients
- **Spring Data JPA** - Database persistence with Hibernate
- **Apache Kafka** - Event streaming
- **PostgreSQL** - Relational database
- **SpringDoc OpenAPI** - API documentation (Swagger)
- **JUnit & Mockito** - Testing framework
- **Docker** - Containerization

### Frontend Services

- **Single-SPA** - Micro-frontend orchestration
- **Angular** - Card marketplace MFE
- **React** - User management MFE
- **TypeScript** - Type-safe JavaScript
- **Webpack** - Module bundling
- **Docker** - Containerization

### Infrastructure

- **Docker Compose** - Multi-container orchestration
- **PostgreSQL+** - Relational database
- **Apache Kafka** - Event streaming platform
- **Nginx** - Web server (production)

## Services Documentation

### Backend Services

#### 1. Eureka Discovery Server (Port 8761)

Service registry that enables microservices to discover and communicate with each other.

**Features:**
- Dynamic service registration
- Health monitoring
- Load balancing support
- Service instance tracking

**Access:** http://localhost:8761

#### 2. Card Service (Port 8081)

Manages trading card data from TCGCSV/TCGPlayer API.

**Features:**
- Sync card data from external API
- Search and filter cards
- Card details retrieval
- Product filtering (excludes non-card items)

**Key Endpoints:**
- `POST /api/cards/sync/{categoryId}/{setId}` - Sync cards from TCGCSV
- `GET /api/cards` - List all cards
- `GET /api/cards?name={name}` - Search cards by name
- `GET /api/cards/{cardId}` - Get card details

**Documentation:** http://localhost:8081/swagger-ui/index.html

#### 3. Listing Service (Port 8082)

Handles card trade listings in marketplace-style format.

**Features:**
- Create, update, delete listings
- Filter by status, owner, card
- Listing lifecycle management (active, completed, cancelled)
- Kafka event publishing
- Integration with Card and User services via Feign

**Key Endpoints:**
- `POST /api/listings` - Create listing
- `GET /api/listings` - Get all listings
- `GET /api/listings/active` - Get active listings
- `GET /api/listings/user/{userId}` - Get user's listings
- `PUT /api/listings/{id}` - Update listing
- `POST /api/listings/{id}/complete` - Complete listing
- `POST /api/listings/{id}/cancel` - Cancel listing
- `DELETE /api/listings/{id}` - Delete listing

**Kafka Events:**
- `listing-created` - New listing created
- `listing-updated` - Listing modified
- `listing-status-changed` - Status changed
- `listing-deleted` - Listing removed

**Documentation:** http://localhost:8082/swagger-ui/index.html

#### 4. User Service (Port 8083)

Manages user accounts, authentication, and profile information.

**Features:**

- User registration with password hashing (BCrypt)
- User profile management (CRUD operations)
- Location-based user data (latitude/longitude)
- Role-based user types (USER/ADMIN)
- Unique username and email validation
- Password security with BCrypt encryption

**Key Endpoints:**

`POST /api/users` - Create new user
`GET /api/users` - Get all users
`GET /api/users/{userId}` - Get user by ID
`GET /api/users/username/{username}` - Get user by username
`GET /api/users/email/{email}` - Get user by email
`PUT /api/users/{userId}` - Update user profile
`DELETE /api/users/{userId}` - Delete user

Documentation: http://localhost:8083/swagger-ui/index.html

### Frontend Services

#### 1. Root Config (Port 9000)

Single-SPA orchestrator that manages all micro-frontends.

**Features:**
- Application routing
- MFE lifecycle management
- Global layout (header, footer)
- Error boundary
- Health monitoring
- Environment configuration

**Routes:**
- `/` - Home page
- `/cards/*` - Card marketplace (Angular)
- `/auth/*` - Authentication (React)
- `/profile/*` - User profile (React)
- `/settings/*` - User settings (React)

**Access:** http://localhost:9000

#### 2. Angular Cards MFE (Port 4201)

Card marketplace browsing and management interface.

**Features:**
- Card listing grid/list view
- Card detail pages
- Search and filtering
- Cart integration (planned)

**Routes:**
- `/cards` - Card listing
- `/cards/:id` - Card details
- `/cards/search` - Search page

**Status:** ðŸš§ In Progress - Phase 2

#### 3. React Users MFE (Port 4202)

User authentication and profile management interface.

**Features:**
- Login/registration forms
- User profile display
- Account settings
- Session management
- JWT token handling

**Routes:**
- `/auth` - Login/register
- `/profile` - User profile
- `/settings` - Account settings

**Status:** ðŸš§ Planned - Phase 3

### Database

**PostgreSQL (Port 5432)**

Database: `marketp_db`

**Tables:**
- `cards` - Card information from TCGCSV
- `listings` - User trade listings
- `appUser` - User accounts (planned)

## API Reference

### Complete Backend API Endpoints

#### Card Service (Port 8081)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/cards/sync/{categoryId}/{setId}` | Sync cards from TCGCSV API |
| GET | `/api/cards` | Get all cards |
| GET | `/api/cards?name={name}` | Search cards by name |
| GET | `/api/cards/{cardId}` | Get card by ID |

#### Listing Service (Port 8082)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/listings` | Create new listing |
| GET | `/api/listings` | Get all listings |
| GET | `/api/listings/{listingId}` | Get listing by ID |
| GET | `/api/listings/active` | Get active listings |
| GET | `/api/listings/user/{ownerUserId}` | Get user's listings |
| GET | `/api/listings/card/{cardId}` | Get listings for card |
| GET | `/api/listings/status/{status}` | Get listings by status |
| PUT | `/api/listings/{listingId}` | Update listing |
| POST | `/api/listings/{listingId}/complete` | Complete listing |
| POST | `/api/listings/{listingId}/cancel` | Cancel listing |
| DELETE | `/api/listings/{listingId}` | Delete listing |

### Example Requests

#### Create a Listing

```bash
curl -X POST http://localhost:8082/api/listings \
  -H "Content-Type: application/json" \
  -d '{
    "ownerUserId": 1,
    "cardId": 100,
    "conditionRating": 8
  }'
```

#### Search Cards

```bash
curl http://localhost:8081/api/cards?name=Pikachu
```

## Development

### Backend Development

#### Running Individual Services Locally

```bash
# Navigate to service directory
cd backend/card-service  # or listing-service

# Run with Maven
mvn spring-boot:run

# Run tests
mvn test

# Build JAR
mvn clean package
```

#### Environment Variables

See individual service `.env` files or `application.yml` for configuration options.

#### Code Coverage

Minimum 60% line coverage enforced by JaCoCo:

```bash
mvn test jacoco:report
open target/site/jacoco/index.html
```

### Frontend Development

#### Running MFEs Locally

**Option 1: Run All Services**

```bash
cd frontend

# Terminal 1 - Root Config
cd root-config && npm start

# Terminal 2 - Angular Cards
cd mfe-angular-cards && npm start

# Terminal 3 - React Users
cd mfe-react-users && npm start
```

**Option 2: Docker Compose**

```bash
cd frontend
docker-compose up
```

#### Development Features

- **Hot Module Replacement** - Auto-reload on changes
- **Import Map Overrides** - Debug tool for MFE switching
- **Error Boundary** - Global error handling
- **Health Checks** - Service monitoring

For detailed frontend setup, see [frontend/DEVELOPMENT.md](frontend/DEVELOPMENT.md)

## Testing

### Backend Testing

```bash
# Run all backend tests
cd backend
mvn test

# Run specific service tests
cd listing-service
mvn test

# Generate coverage report
mvn test jacoco:report
```

### Frontend Testing

```bash
# Root Config
cd frontend/root-config
npm test

# Angular MFE
cd frontend/mfe-angular-cards
npm test

# React MFE
cd frontend/mfe-react-users
npm test
npm run test:coverage
```

## Deployment

### Docker Deployment

All services include Dockerfiles and are orchestrated via Docker Compose.

**Build and Deploy:**

```bash
# Build all images
docker-compose build

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### CI/CD Pipeline

Jenkins pipeline configuration available in `Jenkinsfile` for:

- Automated testing
- Docker image building
- Code coverage reporting
- Deployment to staging/production

For Docker registry configuration, see [frontend/DOCKER_REGISTRY.md](frontend/DOCKER_REGISTRY.md)

## Key Features

### Event-Driven Architecture

Services communicate asynchronously via Kafka:

- **Listing Service** publishes events on listing changes
- **Card Service** can consume events for inventory updates
- **User Service** can react to listing and trade events

### Service Discovery

Eureka enables:

- Automatic service registration
- Dynamic load balancing
- Health monitoring
- Fault tolerance

### Micro-Frontend Benefits

- Independent deployment of UI features
- Technology diversity (Angular + React)
- Team autonomy
- Scalable frontend architecture

## Troubleshooting

### Common Issues

**Port Already in Use:**
```bash
# Find process using port
lsof -ti:8081

# Kill process
kill -9 <PID>
```

**Database Connection Failed:**
- Verify PostgreSQL is running: `docker ps`
- Check credentials in `.env`
- Ensure port 5432 is not blocked

**Service Not Registering with Eureka:**
- Check Eureka is running at http://localhost:8761
- Verify network connectivity between containers
- Check service logs: `docker-compose logs <service-name>`

**MFE Not Loading:**
- Verify MFE is running on correct port
- Check import map in root-config
- Inspect browser console for errors
- Verify CORS configuration

For detailed troubleshooting, see:
- Backend: Individual service README files
- Frontend: [frontend/DEVELOPMENT.md](frontend/DEVELOPMENT.md)

## Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Single-SPA Documentation](https://single-spa.js.org/)
- [Angular Documentation](https://angular.io/docs)
- [React Documentation](https://react.dev/)
- [Docker Documentation](https://docs.docker.com/)