# P3-Group2

## Quick Start

This project uses Docker Compose for easy setup of the Card Service and its database.

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
   This will:
   - Start a PostgreSQL database container.
   - Build and start the Card Service container.

3. **Verify**
   - **Swagger UI**: Access the API documentation at [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
   - **Database**: The database runs on port `5432` (mapped locally as per `.env`).

### Services
- **Card Service**: `http://localhost:8081`
- **Database**: PostgreSQL (`marketp_db`)