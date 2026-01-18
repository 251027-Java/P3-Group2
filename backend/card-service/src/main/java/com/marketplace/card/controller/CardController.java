// Generated with assistance from Antigravity
// Reviewed and modified by Liam Ruiz
package com.marketplace.card.controller;

import com.marketplace.card.model.Card;
import com.marketplace.card.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for the Card Service.
 * Exposes endpoints to:
 *     Trigger synchronization of card data.
 *     Search and retrieve card details.
 */
@RestController
@RequestMapping("/api/cards")
@Tag(name = "Card Service", description = "API for collecting and querying cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    /**
     * Triggers the collection of cards from the external source for a given Set ID.
     *
     * @param categoryId The category ID (e.g. 3 for Pokemon).
     * @param setId      The Set ID (Group ID) to sync.
     * @return Response confirmation message.
     */
    @PostMapping("/sync/{categoryId}/{setId}")
    @Operation(
            summary = "Sync Cards",
            description = "Triggers collection of cards from TCGPlayer for a specific Set ID")
    public ResponseEntity<String> syncCards(@PathVariable Integer categoryId, @PathVariable Integer setId) {
        cardService.syncCards(categoryId, setId);
        return ResponseEntity.ok("Sync requested for Set ID: " + setId);
    }

    /**
     * Retrieves cards, optionally filtered by name.
     *
     * @param name Partial name to search for.
     * @return List of Cards.
     */
    @GetMapping
    @Operation(summary = "Search Cards", description = "Get all cards or search by name")
    public ResponseEntity<List<Card>> getCards(@RequestParam(required = false) String name) {
        if (name != null && !name.isEmpty()) {
            return ResponseEntity.ok(cardService.searchCards(name));
        }
        return ResponseEntity.ok(cardService.getAllCards());
    }

    /**
     * Retrieves a single card by its ID.
     *
     * @param cardId The unique ID of the card.
     * @return THe Card entity if found, or 404 Not Found.
     */
    @GetMapping("/{cardId}")
    @Operation(summary = "Get Card by ID", description = "Get a specific card by its ID")
    public ResponseEntity<Card> getCardById(@PathVariable Long cardId) {
        Optional<Card> card = cardService.getCardById(cardId);
        return ResponseEntity.of(card);
        // if (card.isPresent()) {
        //     return ResponseEntity.ok(card.get());
        // } else {
        //     return ResponseEntity.notFound().build();
        // }
    }
}
