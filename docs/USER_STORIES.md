# User Stories – Pokémon Card Marketplace

This document describes the user stories fulfilled by the Pokémon Card Marketplace application.  
User stories are written from the perspective of end users and administrators and represent the functional requirements supported by the system.

---

## User Roles

- **Guest** – Unauthenticated visitor
- **Registered User** – Authenticated marketplace user
- **Seller** – User who creates card listings
- **Buyer / Trader** – User who initiates trade requests
- **Administrator** – User with elevated privileges

---

## Authentication & Authorization

### User Story 1: User Registration
As a new user,  
I want to create an account,  
So that I can access marketplace features such as listings and trades.

**Acceptance Criteria**
- User can register using email, username, and password
- Email must be unique
- Password is securely stored
- User receives a JWT upon successful registration

---

### User Story 2: User Login
As a registered user,  
I want to log in to my account,  
So that I can securely access protected features.

**Acceptance Criteria**
- User can log in using valid credentials
- Invalid credentials are rejected
- A JWT is issued upon successful authentication

---

### User Story 3: Secure Access
As an authenticated user,  
I want my session to be secure,  
So that unauthorized users cannot access protected resources.

**Acceptance Criteria**
- JWT is required for protected endpoints
- Token expiration is enforced
- Role-based access is supported

---

## User Management

### User Story 4: View User Profile
As a registered user,  
I want to view my user profile,  
So that I can see my account details.

**Acceptance Criteria**
- User can retrieve their account information
- Profile includes username, email, role, and location (if provided)

---

### User Story 5: Update User Information
As a registered user,  
I want to update my account details,  
So that my information stays current.

**Acceptance Criteria**
- User can update email, username, and location
- Invalid updates are rejected
- Changes persist successfully

---

### User Story 6: Delete Account
As a registered user,  
I want to delete my account,  
So that I can permanently remove my data.

**Acceptance Criteria**
- User account can be deleted
- Related system data is handled appropriately
- Deleted users can no longer authenticate

---

## Card Browsing & Data Management

### User Story 7: Browse Trading Cards
As a user,  
I want to browse Pokémon cards,  
So that I can see what cards are available in the marketplace.

**Acceptance Criteria**
- Cards can be retrieved from the system
- Card data includes name and identifying details

---

### User Story 8: Search Cards
As a user,  
I want to search for cards by name,  
So that I can quickly find specific Pokémon cards.

**Acceptance Criteria**
- Users can search cards by name
- Partial matches are supported
- Results are returned efficiently

---

### User Story 9: Sync Card Data
As an administrator or system operator,  
I want to sync card data from an external source,  
So that the marketplace has up-to-date card information.

**Acceptance Criteria**
- Cards can be imported by category and set
- Non-card products are excluded
- Imported data is stored persistently

---

## Listings Management

### User Story 10: Create a Listing
As a seller,  
I want to create a listing for a Pokémon card,  
So that other users can trade with me.

**Acceptance Criteria**
- Seller can create a listing with card and condition details
- Listing is marked as ACTIVE
- Listing is stored persistently

---

### User Story 11: View Listings
As a user,  
I want to view available listings,  
So that I can browse potential trades.

**Acceptance Criteria**
- Users can retrieve all listings
- Users can filter listings by status, user, or card

---

### User Story 12: Update a Listing
As a seller,  
I want to update my listing,  
So that I can change its condition or status.

**Acceptance Criteria**
- Seller can update listing condition
- Only valid updates are allowed
- Listing status remains consistent

---

### User Story 13: Cancel a Listing
As a seller,  
I want to cancel a listing,  
So that it is no longer available for trade.

**Acceptance Criteria**
- Listing status changes to CANCELLED
- Cancelled listings are no longer active

---

### User Story 14: Complete a Listing
As a seller,  
I want to mark a listing as completed,  
So that it reflects a successful trade.

**Acceptance Criteria**
- Listing status changes to COMPLETED
- Completed listings are no longer active

---

### User Story 15: Delete a Listing
As a seller,  
I want to delete a listing,  
So that invalid or unwanted listings are removed.

**Acceptance Criteria**
- Listing can be deleted
- Related events are published
- Deleted listings are no longer retrievable

---

## Trade Management

### User Story 16: Create Trade Request
As a buyer,  
I want to request a trade on a listing,  
So that I can offer my cards in exchange.

**Acceptance Criteria**
- Trade request references a listing
- Trade includes offered card IDs
- Trade is stored with CREATED status

---

### User Story 17: View Trade Requests
As a user,  
I want to view my trade requests,  
So that I can track my trade activity.

**Acceptance Criteria**
- Users can retrieve trades by user
- Users can retrieve trades by listing

---

### User Story 18: Accept Trade Request
As a listing owner,  
I want to accept a trade request,  
So that the trade can be completed.

**Acceptance Criteria**
- Only listing owner can accept
- Trade status updates to ACCEPTED
- Listing status updates accordingly

---

### User Story 19: Decline Trade Request
As a listing owner,  
I want to decline a trade request,  
So that I can reject unwanted offers.

**Acceptance Criteria**
- Trade status updates to DECLINED
- Trade requester is notified via system state

---

## Event-Driven Behavior

### User Story 20: System Event Publishing
As the system,  
I want to publish events when important actions occur,  
So that services stay synchronized.

**Acceptance Criteria**
- Events are published for listing and trade lifecycle changes
- Kafka is used for asynchronous communication

---

### User Story 21: System Event Consumption
As the system,  
I want to react to events from other services,  
So that related data stays consistent.

**Acceptance Criteria**
- Services consume relevant events
- System state updates appropriately

---

## Infrastructure & Architecture

### User Story 22: Centralized Access
As a user,  
I want to access the system through a single entry point,  
So that service interactions are seamless.

**Acceptance Criteria**
- API Gateway routes requests
- Authentication is enforced centrally

---

### User Story 23: Service Discovery
As the system,  
I want services to discover each other dynamically,  
So that the system scales reliably.

**Acceptance Criteria**
- Services register with Eureka
- Inter-service communication uses discovery

---

## Summary

The Pokémon Card Marketplace fulfills all core marketplace functionality including authentication, user management, card browsing, listings, trade workflows, and event-driven communication using a microservices architecture.
