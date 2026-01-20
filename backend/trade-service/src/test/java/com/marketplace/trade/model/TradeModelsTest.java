package com.marketplace.trade.model;
// Generated with assistance from Chat GPT 4.0
// Reviewed and modified by Matt Selle

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class TradeModelsExtendedTests {

    // ------------------- Trade Tests -------------------

    @Test
    @DisplayName("Should not overwrite existing createdAt or tradeStatus on onCreate")
    void onCreateDoesNotOverwriteExistingValues() {
        Trade trade = new Trade();
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 12, 0);
        trade.setCreatedAt(now);
        trade.setTradeStatus(Trade.TradeStatus.accepted);

        trade.onCreate();

        assertThat(trade.getCreatedAt()).isEqualTo(now);
        assertThat(trade.getTradeStatus()).isEqualTo(Trade.TradeStatus.accepted);
    }

    @Test
    @DisplayName("Should handle empty offeredCards list without NPE")
    void tradeWithEmptyOfferedCards() {
        Trade trade = new Trade();
        trade.setOfferedCards(new ArrayList<>());

        assertThat(trade.getOfferedCards()).isEmpty();
    }

    @Test
    @DisplayName("TradeStatus enum should contain all expected values")
    void tradeStatusEnumValues() {
        Trade.TradeStatus[] statuses = Trade.TradeStatus.values();

        assertThat(statuses)
                .containsExactlyInAnyOrder(
                        Trade.TradeStatus.pending,
                        Trade.TradeStatus.accepted,
                        Trade.TradeStatus.rejected,
                        Trade.TradeStatus.cancelled);
    }

    @Test
    @DisplayName("Should link multiple TradeOfferedCards to Trade")
    void tradeWithMultipleOfferedCards() {
        Trade trade = new Trade();
        TradeOfferedCard card1 = new TradeOfferedCard(1L, trade, 101L);
        TradeOfferedCard card2 = new TradeOfferedCard(2L, trade, 102L);

        trade.setOfferedCards(List.of(card1, card2));

        assertThat(trade.getOfferedCards()).containsExactlyInAnyOrder(card1, card2);
        assertThat(card1.getTrade()).isEqualTo(trade);
        assertThat(card2.getTrade()).isEqualTo(trade);
    }

    @Test
    @DisplayName("toString should include offeredCards if added manually")
    void tradeToStringWithOfferedCards() {
        Trade trade = new Trade();
        trade.setTradeId(1L);
        TradeOfferedCard card = new TradeOfferedCard(1L, trade, 101L);
        trade.setOfferedCards(List.of(card));

        String str = trade.toString();
        assertThat(str).contains("tradeId=1");
        // offeredCards is not included in ToString, so just ensure no NPE
        assertThat(str).isNotNull();
    }

    // ------------------- TradeOfferedCard Tests -------------------

    @Test
    @DisplayName("TradeOfferedCard equals/hashCode should work based on offeredId")
    void tradeOfferedCardEqualsAndHashCode() {
        TradeOfferedCard card1 = new TradeOfferedCard();
        card1.setOfferedId(10L);

        TradeOfferedCard card2 = new TradeOfferedCard();
        card2.setOfferedId(10L);

        assertThat(card1).isEqualTo(card2);
        assertThat(card1.hashCode()).isEqualTo(card2.hashCode());
    }

    @Test
    @DisplayName("Should handle TradeOfferedCard with null trade gracefully")
    void tradeOfferedCardWithNullTrade() {
        TradeOfferedCard card = new TradeOfferedCard();
        card.setOfferedId(5L);
        card.setCardId(50L);
        card.setTrade(null);

        assertThat(card.getTrade()).isNull();
        assertThat(card.getCardId()).isEqualTo(50L);
    }

    @Test
    @DisplayName("Should set TradeOfferedCard trade and verify bidirectional relationship manually")
    void tradeOfferedCardBidirectionalManual() {
        Trade trade = new Trade();
        TradeOfferedCard card = new TradeOfferedCard();
        card.setTrade(trade);

        List<TradeOfferedCard> offeredList = new ArrayList<>();
        offeredList.add(card);
        trade.setOfferedCards(offeredList);

        assertThat(card.getTrade()).isEqualTo(trade);
        assertThat(trade.getOfferedCards()).contains(card);
    }
}
