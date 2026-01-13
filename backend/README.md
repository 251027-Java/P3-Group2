# Backend Folder

## API
### Sync Data
`POST /api/cards/sync/{categoryId}/{setId}` = Syncs card data from TCGCSV API 
- `categoryId`: 3 (Pokemon)
- `setId`: e.g. 24325 (Scarlet & Violet: Black Bolt)

### Get Cards
`GET /api/cards` = Returns all cards currently synced

`GET /api/cards?name=Pikachu` = Returns cards with name "Pikachu"

`GET /api/cards/{cardId}` = Returns a specific card by ID

## MicroServices Architecture