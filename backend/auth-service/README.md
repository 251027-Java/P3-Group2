# Auth Service

This service handles user authentication and JWT token generation.

## Features
- User registration (mocked - will integrate with AppUser-service).
- User login with JWT token generation.
- Token validation utilities.

## Configuration
Configuration in `application.yaml`:
- `jwt.secret`: Secret key for signing tokens (set via `JWT_SECRET` env var).
- `jwt.expiration`: Token expiration time in milliseconds (default: 24 hours).

## API

### Register
`POST /api/auth/register`
```json
{
  "email": "user@example.com",
  "username": "username",
  "passwordHash": "password123"
}
```

### Login
`POST /api/auth/login`
```json
{
  "email": "user@example.com",
  "passwordHash": "password123"
}
```

### Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400000,
  "username": "username"
}
```

## Test User
A test user is seeded on startup:
- **Email**: `test@example.com`
- **Password**: `password123`

## Testing
Run tests with:
```bash
mvn test
```

## AI Contribution
Files containing AI-generated code are marked with a header:
```java
// Generated with assistance from Antigravity through Gemini
// Reviewed and modified by Liam Ruiz
```
