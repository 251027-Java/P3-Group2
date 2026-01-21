package com.marketplace.card.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.marketplace.card.model.Card;
import com.marketplace.card.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * Unit tests for CardController using MockMvc.
 */
@WebMvcTest(CardController.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    private Card testCard;

    @BeforeEach
    void setUp() {
        testCard = new Card();
        testCard.setCardId(1L);
        testCard.setName("Pikachu");
        testCard.setCleanName("Pikachu");
        testCard.setImageUrl("https://example.com/pikachu.jpg");
        testCard.setSetId(100);
        testCard.setCategoryId(3);
        testCard.setPrice(new BigDecimal("10.50"));
    }

    @Nested
    @DisplayName("Sync Endpoint Tests")
    class SyncEndpointTests {

        @Test
        @DisplayName("POST /api/cards/sync/{categoryId}/{setId} - should return success message")
        void syncCards_ValidRequest_ReturnsSuccessMessage() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/api/cards/sync/3/100"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Sync requested for Set ID: 100"));

            verify(cardService).syncCards(3, 100);
        }

        @Test
        @DisplayName("POST /api/cards/sync - should handle different category and set IDs")
        void syncCards_DifferentIds_CallsServiceWithCorrectParams() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/api/cards/sync/5/250"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Sync requested for Set ID: 250"));

            verify(cardService).syncCards(5, 250);
        }
    }

    @Nested
    @DisplayName("Get Cards Endpoint Tests")
    class GetCardsEndpointTests {

        @Test
        @DisplayName("GET /api/cards - should return all cards")
        void getCards_NoNameParam_ReturnsAllCards() throws Exception {
            // Arrange
            Card card2 = new Card();
            card2.setCardId(2L);
            card2.setName("Charizard");
            when(cardService.getAllCards()).thenReturn(Arrays.asList(testCard, card2));

            // Act & Assert
            mockMvc.perform(get("/api/cards"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].name").value("Pikachu"))
                    .andExpect(jsonPath("$[1].name").value("Charizard"));
        }

        @Test
        @DisplayName("GET /api/cards?name=X - should return filtered cards")
        void getCards_WithNameParam_ReturnsFilteredCards() throws Exception {
            // Arrange
            when(cardService.searchCards("pika")).thenReturn(Collections.singletonList(testCard));

            // Act & Assert
            mockMvc.perform(get("/api/cards").param("name", "pika"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].name").value("Pikachu"));

            verify(cardService).searchCards("pika");
        }

        @Test
        @DisplayName("GET /api/cards - should return empty list when no cards")
        void getCards_NoCards_ReturnsEmptyList() throws Exception {
            // Arrange
            when(cardService.getAllCards()).thenReturn(Collections.emptyList());

            // Act & Assert
            mockMvc.perform(get("/api/cards"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("Get Card By ID Endpoint Tests")
    class GetCardByIdEndpointTests {

        @Test
        @DisplayName("GET /api/cards/{cardId} - should return card when found")
        void getCardById_CardExists_ReturnsCard() throws Exception {
            // Arrange
            when(cardService.getCardById(1L)).thenReturn(Optional.of(testCard));

            // Act & Assert
            mockMvc.perform(get("/api/cards/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.cardId").value(1))
                    .andExpect(jsonPath("$.name").value("Pikachu"))
                    .andExpect(jsonPath("$.price").value(10.50));
        }

        @Test
        @DisplayName("GET /api/cards/{cardId} - should return 404 when not found")
        void getCardById_CardNotFound_Returns404() throws Exception {
            // Arrange
            when(cardService.getCardById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            mockMvc.perform(get("/api/cards/999"))
                    .andExpect(status().isNotFound());
        }
    }
}
