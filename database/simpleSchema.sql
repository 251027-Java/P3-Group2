-- Sometimes the auto converter doesn't like weird key words so here
-- is only the strictly necessary information
-- 1. Users Table
CREATE TABLE appUser (
    appUserId INT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    username VARCHAR(50) NOT NULL,
    latitude DECIMAL(10, 8), 
    longitude DECIMAL(11, 8),
    passwordHash VARCHAR(255) NOT NULL,
    createdAt TIMESTAMP NOT NULL
);

-- 2. Cards Reference Table
CREATE TABLE cards (
    cardId INT PRIMARY KEY,
    cardName VARCHAR(255) NOT NULL,
    marketValue DECIMAL(12, 2)
);

-- 3. Listings Table
CREATE TABLE listings (
    listingId INT PRIMARY KEY,
    ownerUserId INT NOT NULL,
    cardId INT NOT NULL,
    condition_rating INT NOT NULL,
    listingStatus VARCHAR(50) NOT NULL,
    createdAt TIMESTAMP NOT NULL,
    CONSTRAINT fk_listing_owner FOREIGN KEY (ownerUserId) REFERENCES appUser(appUserId),
    CONSTRAINT fk_listing_card FOREIGN KEY (cardId) REFERENCES cards(cardId)
);

-- 4. Trades Table
CREATE TABLE trades (
    tradeId INT PRIMARY KEY,
    listingId INT NOT NULL,
    requestingUserId INT NOT NULL,
    tradeStatus VARCHAR(50) NOT NULL,
    createdAt TIMESTAMP NOT NULL,
    CONSTRAINT fk_trade_listing FOREIGN KEY (listingId) REFERENCES listings(listingId),
    CONSTRAINT fk_trade_requester FOREIGN KEY (requestingUserId) REFERENCES appUser(appUserId)
);

-- 5. Junction: What the seller wants
CREATE TABLE ListingDesiredCards (
    desiredId INT PRIMARY KEY,
    listingId INT NOT NULL,
    cardId INT NOT NULL,
    CONSTRAINT fk_desired_listing FOREIGN KEY (listingId) REFERENCES listings(listingId),
    CONSTRAINT fk_desired_card FOREIGN KEY (cardId) REFERENCES cards(cardId)
);

-- 6. Junction: What the buyer offers
CREATE TABLE TradeOfferedCards (
    offeredId INT PRIMARY KEY,
    tradeId INT NOT NULL,
    cardId INT NOT NULL,
    CONSTRAINT fk_offered_trade FOREIGN KEY (tradeId) REFERENCES trades(tradeId),
    CONSTRAINT fk_offered_card FOREIGN KEY (cardId) REFERENCES cards(cardId)
);