-- 1. Users Table
CREATE TABLE appUser (
    appUserId INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    -- Separate fields allow for easier distance calculations
    latitude DECIMAL(10, 8), 
    longitude DECIMAL(11, 8),
    passwordHash VARCHAR(255) NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Cards Reference Table
CREATE TABLE cards (
    cardId INT PRIMARY KEY AUTO_INCREMENT,
    cardName VARCHAR(255) NOT NULL,
    marketValue DECIMAL(12, 2), -- Supports values like 1250.50
    -- Other Info: rarity, set_name, image_url, etc.
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Listings Table
CREATE TABLE listings (
    listingId INT PRIMARY KEY AUTO_INCREMENT,
    ownerUserId INT NOT NULL,
    cardId INT NOT NULL,
    condition_rating INT CHECK (condition_rating BETWEEN 1 AND 10), -- Assuming a 1-10 scale
    listingStatus VARCHAR(50) DEFAULT 'active', -- e.g., 'active', 'completed', 'cancelled'
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ownerUserId) REFERENCES appUser(appUserId),
    FOREIGN KEY (cardId) REFERENCES cards(cardId)
);

-- 4. Trades Table (The offer to swap)
CREATE TABLE trades (
    tradeId INT PRIMARY KEY AUTO_INCREMENT,
    listingId INT NOT NULL,
    requestingUserId INT NOT NULL,
    tradeStatus VARCHAR(50) DEFAULT 'pending', -- e.g., 'pending', 'accepted', 'rejected'
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (listingId) REFERENCES listings(listingId),
    FOREIGN KEY (requestingUserId) REFERENCES appUser(appUserId)
);

-- 5. ListingDesiredCards (What the seller wants in exchange)
CREATE TABLE ListingDesiredCards (
    desiredId INT PRIMARY KEY AUTO_INCREMENT,
    listingId INT NOT NULL,
    cardId INT NOT NULL,
    FOREIGN KEY (listingId) REFERENCES listings(listingId),
    FOREIGN KEY (cardId) REFERENCES cards(cardId)
);

-- 6. TradeOfferedCards (What the buyer is offering for that specific trade)
CREATE TABLE TradeOfferedCards (
    offeredId INT PRIMARY KEY AUTO_INCREMENT,
    tradeId INT NOT NULL,
    cardId INT NOT NULL,
    FOREIGN KEY (tradeId) REFERENCES trades(tradeId),
    FOREIGN KEY (cardId) REFERENCES cards(cardId)
);




