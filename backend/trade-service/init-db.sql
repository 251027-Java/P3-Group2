-- Generated with assistance from Cursor
-- Reviewed and modified by Matt Selle
-- PostgreSQL database initialization script for trade-service testing

-- 1. Users Table
CREATE TABLE IF NOT EXISTS "appUser" (
    "appUserId" SERIAL PRIMARY KEY,
    "email" VARCHAR(255) UNIQUE NOT NULL,
    "username" VARCHAR(50) UNIQUE NOT NULL,
    "latitude" DECIMAL(10, 8), 
    "longitude" DECIMAL(11, 8),
    "passwordHash" VARCHAR(255) NOT NULL,
    "createdAt" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Cards Reference Table
CREATE TABLE IF NOT EXISTS "cards" (
    "cardId" SERIAL PRIMARY KEY,
    "cardName" VARCHAR(255) NOT NULL,
    "marketValue" DECIMAL(12, 2),
    "createdAt" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Listings Table
CREATE TABLE IF NOT EXISTS "listings" (
    "listingId" SERIAL PRIMARY KEY,
    "ownerUserId" INTEGER NOT NULL,
    "cardId" INTEGER NOT NULL,
    "condition_rating" INTEGER CHECK ("condition_rating" BETWEEN 1 AND 10),
    "listingStatus" VARCHAR(50) DEFAULT 'active',
    "createdAt" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY ("ownerUserId") REFERENCES "appUser"("appUserId"),
    FOREIGN KEY ("cardId") REFERENCES "cards"("cardId")
);

-- 4. Trades Table (The offer to swap)
CREATE TABLE IF NOT EXISTS "trades" (
    "tradeId" SERIAL PRIMARY KEY,
    "listingId" INTEGER NOT NULL,
    "requestingUserId" INTEGER NOT NULL,
    "tradeStatus" VARCHAR(50) DEFAULT 'pending',
    "createdAt" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY ("listingId") REFERENCES "listings"("listingId"),
    FOREIGN KEY ("requestingUserId") REFERENCES "appUser"("appUserId")
);

-- 5. ListingDesiredCards (What the seller wants in exchange)
CREATE TABLE IF NOT EXISTS "ListingDesiredCards" (
    "desiredId" SERIAL PRIMARY KEY,
    "listingId" INTEGER NOT NULL,
    "cardId" INTEGER NOT NULL,
    FOREIGN KEY ("listingId") REFERENCES "listings"("listingId"),
    FOREIGN KEY ("cardId") REFERENCES "cards"("cardId")
);

-- 6. TradeOfferedCards (What the buyer is offering for that specific trade)
CREATE TABLE IF NOT EXISTS "TradeOfferedCards" (
    "offeredId" SERIAL PRIMARY KEY,
    "tradeId" INTEGER NOT NULL,
    "cardId" INTEGER NOT NULL,
    FOREIGN KEY ("tradeId") REFERENCES "trades"("tradeId"),
    FOREIGN KEY ("cardId") REFERENCES "cards"("cardId")
);

-- Insert test data (optional - for testing purposes)
INSERT INTO "appUser" ("email", "username", "passwordHash") VALUES
    ('testuser1@example.com', 'testuser1', 'hashedpassword1'),
    ('testuser2@example.com', 'testuser2', 'hashedpassword2')
ON CONFLICT ("email") DO NOTHING;

INSERT INTO "cards" ("cardName", "marketValue") VALUES
    ('Pikachu', 10.50),
    ('Charizard', 150.00),
    ('Blastoise', 120.00),
    ('Venusaur', 110.00)
ON CONFLICT DO NOTHING;

INSERT INTO "listings" ("ownerUserId", "cardId", "condition_rating", "listingStatus") VALUES
    (1, 1, 9, 'active'),
    (1, 2, 8, 'active'),
    (2, 3, 10, 'active')
ON CONFLICT DO NOTHING;
