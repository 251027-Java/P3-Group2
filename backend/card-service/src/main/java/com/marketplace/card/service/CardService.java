// Generated with assistance from Antigravity through Gemini
// Reviewed and modified by Liam Ruiz
package com.marketplace.card.service;

import com.marketplace.card.dto.TcgPriceDto;
import com.marketplace.card.dto.TcgProductDto;
import com.marketplace.card.model.Card;
import com.marketplace.card.repository.CardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service class for managing Card entities.
 * <p>
 * Handles the logic for:
 * <ul>
 *   <li>Synchronizing card data from external TCG sources.</li>
 *   <li>Persisting cards to the database.</li>
 *   <li>Retrieving cards via search or ID.</li>
 * </ul>
 */
@Service
public class CardService {

    private static final Logger log = LoggerFactory.getLogger(CardService.class);
    private final TcgConnectService tcgConnectService;
    private final CardRepository cardRepository;

    public CardService(TcgConnectService tcgConnectService, CardRepository cardRepository) {
        this.tcgConnectService = tcgConnectService;
        this.cardRepository = cardRepository;
    }

    /**
     * Synchronizes cards for a specific Category and Set ID from the external TCG provider.
     * <p>
     * Fetches products and prices, filters out non-card items, and saves/updates records in the database.
     * Uses batch processing to minimize database interactions.
     *
     * @param categoryId TCGPlayer Category ID (e.g., 3 for Pokemon).
     * @param groupId    TCGPlayer Group/Set ID.
     */
    @Transactional
    public void syncCards(Integer categoryId, Integer groupId) {
        log.info("Starting sync for Category: {}, Group: {}", categoryId, groupId);

        // 1. Fetch Products and Prices
        List<TcgProductDto> products = tcgConnectService.fetchProducts(categoryId, groupId);
        List<TcgPriceDto> prices = tcgConnectService.fetchPrices(categoryId, groupId);

        // 2. Map prices by ProductId
        Map<Long, TcgPriceDto> priceMap = prices.stream()
                .collect(Collectors.toMap(TcgPriceDto::getProductId, Function.identity(), (p1, p2) -> p1));

        // 3. Fetch existing cards for this set to avoid N+1 selects
        List<Card> existingCards = cardRepository.findBySetId(groupId);
        Map<Long, Card> existingCardMap =
                existingCards.stream().collect(Collectors.toMap(Card::getCardId, Function.identity()));

        // 4. Map Products to Cards
        List<Card> cardsToSave = products.stream()
                .filter(tcgConnectService::isCard)
                .map(product -> prepareCard(
                        product, priceMap.get(product.getProductId()), existingCardMap.get(product.getProductId())))
                .collect(Collectors.toList());

        // 5. Batch Save
        cardRepository.saveAll(cardsToSave);

        log.info("Sync completed. Processed {} cards.", cardsToSave.size());
    }

    /**
     * Helper method to create or update a Card entity.
     *
     * @param product      The product DTO.
     * @param priceDto     The price DTO.
     * @param existingCard The existing entity from DB, if any.
     * @return The prepared Card entity.
     */
    private Card prepareCard(TcgProductDto product, TcgPriceDto priceDto, Card existingCard) {
        Card card = (existingCard != null) ? existingCard : new Card();

        card.setCardId(product.getProductId());
        card.setName(product.getName());
        card.setCleanName(product.getCleanName());
        card.setImageUrl(product.getImageUrl());
        card.setSetId(product.getGroupId());
        card.setCategoryId(product.getCategoryId());

        if (priceDto != null) {
            // Priority: Market Price > Mid Price
            BigDecimal price = priceDto.getMarketPrice();
            if (price == null) {
                price = priceDto.getMidPrice();
            }
            card.setPrice(price);
        }

        return card;
    }

    /**
     * Retrieves all cards currently persisted in the database.
     *
     * @return List of all Cards.
     */
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    /**
     * Searches for cards matching the partial name (case-insensitive).
     *
     * @param name Name query.
     * @return List of matching Cards.
     */
    public List<Card> searchCards(String name) {
        return cardRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Retrieves a specific card by its unique ID.
     *
     * @param cardId The ID of the card.
     * @return Optional containing the Card if found, or empty.
     */
    public Optional<Card> getCardById(Long cardId) {
        return cardRepository.findById(cardId);
    }
}
