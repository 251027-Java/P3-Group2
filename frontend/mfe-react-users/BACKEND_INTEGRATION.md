# Backend Integration Guide

## Overview
This document outlines the backend integration architecture for the React Users Micro-Frontend (MFE). The frontend communicates with a microservices-based backend through an API Gateway.

## Architecture

### Backend Services
- **API Gateway** (Port 8080) - Entry point for all API calls
- **Auth Service** (Port 8083) - Authentication & JWT token management
- **User Service** (Port 8084) - User profiles & management
- **Trade Service** (Port 8085) - Pokemon card trades
- **Listing Service** (Port 8082) - Marketplace listings
- **Card Service** (Port 8081) - Pokemon card data

### Service Layer Pattern
All backend communication follows a service layer architecture:
```
Component → Service Class → API Client → API Gateway → Microservice
```

## Service Classes

### 1. AuthService (`src/services/AuthService.ts`)
Handles authentication and token management.

**Methods:**
- `login(credentials)` - Authenticate user
- `register(credentials)` - Create new account
- `refreshToken()` - Refresh JWT token
- `logout()` - Clear session

**Usage:**
```typescript
import AuthService from '../services/AuthService';

await AuthService.login({
  email: 'user@example.com',
  password: 'password123',
  rememberMe: true
});
```

### 2. UserService (`src/services/UserService.ts`)
Manages user profiles and settings.

**Methods:**
- `getUserById(userId)` - Get user profile by ID
- `getProfile()` - Get current user's profile
- `updateProfile(data)` - Update profile information
- `uploadAvatar(file)` - Upload profile picture
- `getSettings()` - Get user settings
- `updateSettings(settings)` - Update settings
- `changePassword(data)` - Change password
- `deleteAccount()` - Delete user account

**Usage:**
```typescript
import UserService from '../services/UserService';

const user = await UserService.getUserById('123');
await UserService.updateProfile({ username: 'NewName' });
```

### 3. TradeService (`src/services/TradeService.ts`)
Handles Pokemon card trading operations.

**Methods:**
- `getUserTrades(userId)` - Get all trades for a user
- `getTradeById(tradeId)` - Get specific trade details
- `createTrade(data)` - Create new trade offer
- `updateTradeStatus(tradeId, status)` - Accept/reject/complete trade
- `deleteTrade(tradeId)` - Cancel trade

**Trade Statuses:**
- `PENDING` - Trade offer sent, awaiting response
- `ACCEPTED` - Both parties agreed
- `REJECTED` - Trade declined
- `COMPLETED` - Trade finalized
- `CANCELLED` - Trade cancelled

**Usage:**
```typescript
import TradeService from '../services/TradeService';

const trades = await TradeService.getUserTrades('123');
await TradeService.updateTradeStatus('456', 'ACCEPTED');
```

### 4. ListingService (`src/services/ListingService.ts`)
Manages marketplace listings.

**Methods:**
- `getUserListings(userId)` - Get user's listings
- `getAllListings()` - Get all active listings
- `getListingById(listingId)` - Get specific listing
- `createListing(data)` - Create new listing
- `updateListing(listingId, data)` - Update listing
- `deleteListing(listingId)` - Remove listing

**Listing Statuses:**
- `ACTIVE` - Available for sale
- `SOLD` - Successfully sold
- `PENDING` - Transaction in progress
- `CANCELLED` - Listing cancelled
- `EXPIRED` - Listing expired

**Usage:**
```typescript
import ListingService from '../services/ListingService';

const listings = await ListingService.getUserListings('123');
await ListingService.createListing({
  cardId: '789',
  price: 49.99,
  condition: 'MINT'
});
```

### 5. CardService (`src/services/CardService.ts`)
Provides Pokemon card data and search.

**Methods:**
- `getAllCards()` - Get all cards in database
- `getCardById(cardId)` - Get specific card details
- `getUserCards(userId)` - Get cards owned by user
- `searchCards(query)` - Search cards by name/description
- `filterCardsByType(type)` - Filter by Pokemon type

**Usage:**
```typescript
import CardService from '../services/CardService';

const cards = await CardService.searchCards('Charizard');
const userCards = await CardService.getUserCards('123');
```

## API Client Configuration

### Base Configuration (`src/services/apiClient.ts`)
```typescript
const apiClient = axios.create({
  baseURL: process.env.API_URL || 'http://localhost:8080',
  timeout: 10000,
  withCredentials: false,
  headers: {
    'Content-Type': 'application/json',
  },
});
```

### Request Interceptor
Automatically adds JWT Bearer token to all requests:
```typescript
apiClient.interceptors.request.use((config) => {
  const token = getAuthToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

### Response Interceptor
Handles authentication errors:
- **401 Unauthorized** - Redirects to login
- **403 Forbidden** - Shows access denied error
- **500 Server Error** - Shows server error message

## Authentication Flow

1. **Login**
   ```typescript
   await AuthService.login({ email, password, rememberMe });
   // Token stored in localStorage
   // User redirected to profile
   ```

2. **Token Refresh**
   ```typescript
   // Automatically refreshed before expiry
   // Scheduled based on expiresIn value
   ```

3. **Protected Routes**
   ```typescript
   // All API calls include Bearer token
   // 401 errors redirect to login
   ```

4. **Logout**
   ```typescript
   await AuthService.logout();
   // Clears localStorage
   // Redirects to login
   ```

## Error Handling

All service methods throw errors that should be caught in components:

```typescript
try {
  const user = await UserService.getUserById(userId);
  setUserData(user);
} catch (error: any) {
  const message = error.response?.data?.message || error.message || 'Request failed';
  setError(message);
  console.error('[Component] Error:', error);
}
```

## Environment Configuration

### Development
```bash
# .env.development
API_URL=http://localhost:8080
```

### Production
```bash
# .env.production
API_URL=https://api.pokemarketplace.com
```

### Docker
```yaml
# docker-compose.yml
environment:
  - API_URL=http://api-gateway:8080
```

## TypeScript Interfaces

All services include TypeScript interfaces for type safety:

```typescript
// UserProfile interface
export interface UserProfile {
  id: string;
  username: string;
  email: string;
  avatar?: string;
  role: string;
  joinDate: string;
  lastLogin: string;
}

// Trade interface
export interface Trade {
  tradeId: string;
  initiatorUserId: string;
  receiverUserId: string;
  tradeStatus: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'COMPLETED' | 'CANCELLED';
  // ... other fields
}

// Listing interface
export interface Listing {
  listingId: string;
  userId: string;
  cardId: string;
  price: number;
  listingStatus: 'ACTIVE' | 'SOLD' | 'PENDING' | 'CANCELLED' | 'EXPIRED';
  // ... other fields
}
```

## Testing Backend Connection

### 1. Verify Backend Services Running
```bash
docker ps
# Should show: api-gateway, auth-service, user-service, trade-service, listing-service, card-service
```

### 2. Test Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

### 3. Test Protected Endpoint
```bash
curl http://localhost:8080/api/users/123 \
  -H "Authorization: Bearer <token>"
```

### 4. Check Browser Console
Open DevTools → Network tab → Filter by XHR
- Look for requests to `http://localhost:8080/api/*`
- Verify `Authorization: Bearer <token>` header
- Check response status codes

## Common Issues & Solutions

### CORS Errors
**Issue:** `Access-Control-Allow-Origin` error
**Solution:** Configure backend CORS to allow `http://localhost:9000`

### 401 Unauthorized
**Issue:** Token expired or invalid
**Solution:** Clear localStorage and login again
```typescript
localStorage.clear();
// Navigate to login page
```

### Connection Refused
**Issue:** Backend services not running
**Solution:**
```bash
cd /path/to/backend
docker-compose up -d
```

### Wrong Port
**Issue:** API calls going to wrong service
**Solution:** Always use API Gateway port 8080, not individual service ports

## Best Practices

1. **Always use service layer** - Never call `fetch()` or `axios` directly from components
2. **Handle errors gracefully** - Show user-friendly error messages
3. **Add loading states** - Show spinners during API calls
4. **Log for debugging** - Use `console.log` with component prefix
5. **Type everything** - Use TypeScript interfaces for all data structures
6. **Validate data** - Check for null/undefined before using API responses
7. **Cache when appropriate** - Store frequently accessed data in state/context
8. **Use API Gateway** - Route all calls through port 8080, not direct to services

## Rebuild & Deploy

After making changes:

```bash
# Rebuild frontend container
cd /path/to/P3-Group2
docker-compose build mfe-react-users

# Restart container
docker-compose up -d mfe-react-users

# View logs
docker-compose logs -f mfe-react-users
```

## API Endpoint Reference

### Auth Service
- `POST /api/auth/login` - Login
- `POST /api/auth/register` - Register
- `POST /api/auth/refresh` - Refresh token
- `POST /api/auth/logout` - Logout

### User Service
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/profile` - Get current user
- `PUT /api/users/profile` - Update profile
- `POST /api/users/profile/avatar` - Upload avatar
- `GET /api/users/settings` - Get settings
- `PUT /api/users/settings` - Update settings
- `POST /api/users/change-password` - Change password
- `DELETE /api/users/account` - Delete account

### Trade Service
- `GET /api/trades/user/{userId}` - Get user trades
- `GET /api/trades/{tradeId}` - Get trade by ID
- `POST /api/trades` - Create trade
- `PUT /api/trades/{tradeId}/status` - Update status
- `DELETE /api/trades/{tradeId}` - Delete trade

### Listing Service
- `GET /api/listings/user/{userId}` - Get user listings
- `GET /api/listings` - Get all listings
- `GET /api/listings/{listingId}` - Get listing by ID
- `POST /api/listings` - Create listing
- `PUT /api/listings/{listingId}` - Update listing
- `DELETE /api/listings/{listingId}` - Delete listing

### Card Service
- `GET /api/cards` - Get all cards
- `GET /api/cards/{cardId}` - Get card by ID
- `GET /api/cards/user/{userId}` - Get user's cards
- `GET /api/cards/search?q={query}` - Search cards
- `GET /api/cards/filter?type={type}` - Filter by type

---

**Last Updated:** 2024
**Maintained by:** Development Team
