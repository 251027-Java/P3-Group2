# Card Service

This service collects and manages Trading Card Game data.

## Features
- Collects data from TCGCSV/TCGPlayer.
- Filters out non-card products (Booster boxes, etc.).
- Persists data to database.
- Provides search API.

## Configuration
- Database connection can be configured in `application.yml`.
- Default: PostgreSQL .

## API
### Sync Data
`POST /api/cards/sync/{categoryId}/{setId}`
- `categoryId`: 3 (Pokemon)
- `setId`: e.g. 24325 (Scarlet & Violet: Black Bolt)

### Get Cards
`GET /api/cards`
`GET /api/cards?name=Pikachu`

### Get Card by ID
`GET /api/cards/{cardId}`

## Testing
Run tests with:
```bash
mvn test
```

## AI Contribution
Files containing AI-generated code are marked with a header:
```java
// Generated with assistance from Antigravity
// Reviewed and modified by Liam Ruiz
```
