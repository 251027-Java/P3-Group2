package com.marketplace.trade.dto;
// Generated with assistance from ChatGPT 4.0
// Reviewed and modified by Matt Selle

import static org.junit.jupiter.api.Assertions.*;

import com.marketplace.trade.model.Trade.TradeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

class TradeResponseDTOTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        Long tradeId = 1L;
        Long listingId = 2L;
        Long requestingUserId = 3L;
        TradeStatus status = TradeStatus.pending;
        LocalDateTime createdAt = LocalDateTime.now();
        List<Long> offeredCardIds = List.of(10L, 11L);

        TradeResponseDTO dto =
                new TradeResponseDTO(tradeId, listingId, requestingUserId, status, createdAt, offeredCardIds);

        assertEquals(tradeId, dto.getTradeId());
        assertEquals(listingId, dto.getListingId());
        assertEquals(requestingUserId, dto.getRequestingUserId());
        assertEquals(status, dto.getTradeStatus());
        assertEquals(createdAt, dto.getCreatedAt());
        assertEquals(offeredCardIds, dto.getOfferedCardIds());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        TradeResponseDTO dto = new TradeResponseDTO();

        dto.setTradeId(1L);
        dto.setListingId(2L);
        dto.setRequestingUserId(3L);
        dto.setTradeStatus(TradeStatus.accepted);
        LocalDateTime now = LocalDateTime.now();
        dto.setCreatedAt(now);
        dto.setOfferedCardIds(List.of(20L, 21L));

        assertEquals(1L, dto.getTradeId());
        assertEquals(2L, dto.getListingId());
        assertEquals(3L, dto.getRequestingUserId());
        assertEquals(TradeStatus.accepted, dto.getTradeStatus());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(List.of(20L, 21L), dto.getOfferedCardIds());
    }

    @Test
    void testEqualsAndHashCodeBasic() {
        TradeResponseDTO dto1 =
                new TradeResponseDTO(1L, 2L, 3L, TradeStatus.pending, LocalDateTime.now(), List.of(10L));
        TradeResponseDTO dto2 =
                new TradeResponseDTO(1L, 2L, 3L, TradeStatus.pending, dto1.getCreatedAt(), List.of(10L));

        assertEquals(dto1.getTradeId(), dto2.getTradeId());
        assertEquals(dto1.getListingId(), dto2.getListingId());
        assertEquals(dto1.getRequestingUserId(), dto2.getRequestingUserId());
        assertEquals(dto1.getTradeStatus(), dto2.getTradeStatus());
        assertEquals(dto1.getCreatedAt(), dto2.getCreatedAt());
        assertEquals(dto1.getOfferedCardIds(), dto2.getOfferedCardIds());
    }
}
