// Generated with assistance from Antigravity through Gemini
// Reviewed and modified by Liam Ruiz
package com.marketplace.card.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.marketplace.card.dto.TcgPriceDto;
import com.marketplace.card.dto.TcgProductDto;
import com.marketplace.card.model.Card;
import com.marketplace.card.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private TcgConnectService tcgConnectService;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    private TcgProductDto productCard;
    private TcgProductDto productBooster;
    private TcgPriceDto priceDto;

    @BeforeEach
    void setUp() {
        productCard = new TcgProductDto();
        productCard.setProductId(1L);
        productCard.setName("Pikachu");
        productCard.setCleanName("Pikachu");
        productCard.setGroupId(100);
        productCard.setCategoryId(3);

        productBooster = new TcgProductDto();
        productBooster.setProductId(2L);
        productBooster.setName("Booster Box");

        priceDto = new TcgPriceDto();
        priceDto.setProductId(1L);
        priceDto.setMarketPrice(new BigDecimal("10.50"));
    }

    @Test
    void testSyncCards_SavesOnlyCards() {
        // Arrange
        when(tcgConnectService.fetchProducts(3, 100)).thenReturn(Arrays.asList(productCard, productBooster));
        when(tcgConnectService.fetchPrices(3, 100)).thenReturn(Collections.singletonList(priceDto));
        when(tcgConnectService.isCard(productCard)).thenReturn(true);
        when(tcgConnectService.isCard(productBooster)).thenReturn(false);

        // Mock existing card check (empty list -> new cards)
        when(cardRepository.findBySetId(100)).thenReturn(Collections.emptyList());

        // Act
        cardService.syncCards(3, 100);

        // Assert
        verify(cardRepository, times(1)).saveAll(anyList());
        verify(tcgConnectService, times(1)).isCard(productCard);
        verify(tcgConnectService, times(1)).isCard(productBooster);
    }

    @Test
    void testGetAllCards() {
        // Arrange
        Card card = new Card();
        card.setName("Test");
        when(cardRepository.findAll()).thenReturn(Collections.singletonList(card));

        // Act
        List<Card> result = cardService.getAllCards();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getName());
    }

    @Test
    void testSearchCards() {
        // Arrange
        Card card = new Card();
        card.setName("Pikachu");
        when(cardRepository.findByNameContainingIgnoreCase("pika")).thenReturn(Collections.singletonList(card));

        // Act
        List<Card> result = cardService.searchCards("pika");

        // Assert
        assertEquals(1, result.size());
    }
}
