# Card Service

This service collects and manages Trading Card Game data.

## Features
- Collects data from TCGCSV/TCGPlayer.
- Filters out non-card products (Booster boxes, etc.).
- Persists data to database.
- Provides search API.

## Setup

As of right now, there is no seeding of the database. To actually use the service, you will need to populate the database with card data. This can be done by running the sync endpoint:

`POST /api/cards/sync/{categoryId}/{setId}`

Where `categoryId` is the category ID (e.g. 3 for Pokemon) and `setId` is the Set ID (24325 for Scarlet & Violet: Black Bolt).

## Configuration
- Database connection can be configured in `application.yaml`.
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
