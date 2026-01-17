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

### Listings Endpoints

`DELETE /api/listings/{listingId}` = Delete a listing

`GET /api/listings/{listingId}` = Get listing by ID

`GET /api/listings` = Get all listings

`GET /api/listings/user/{ownerUserId}` = Get listings by owner

`GET /api/listings/status/{status}` = Get listings by status

`GET /api/listings/card/{cardId}` = Get listings by card

`GET /api/listings/active` = Get all active listings

`POST /api/listings` = Create a new listing

`POST /api/listings/{listingId}/complete` = Complete a listing

`POST /api/listings/{listingId}/cancel` = Cancel a listing

`PUT /api/listings/{listingId}` = Update a listing

## MicroServices Architecture