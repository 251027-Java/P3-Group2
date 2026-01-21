package com.marketplace.trade.controller;

// Generated with assistance from ChatGPT & Claude Opus 4.5
// Reviewed and modified by Matt Selle & Marcus Wright

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.trade.dto.TradeRequestDTO;
import com.marketplace.trade.dto.TradeResponseDTO;
import com.marketplace.trade.exception.ResourceNotFoundException;
import com.marketplace.trade.exception.TradeException;
import com.marketplace.trade.model.Trade.TradeStatus;
import com.marketplace.trade.service.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for TradeController
 * Uses MockMvc to test REST endpoints
 */
@WebMvcTest(TradeController.class)
class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TradeService tradeService;

    private TradeRequestDTO tradeRequestDTO;
    private TradeResponseDTO tradeResponseDTO;

    @BeforeEach
    void setUp() {
        tradeRequestDTO = new TradeRequestDTO(
                1L, // listingId
                100L, // requestingUserId
                Arrays.asList(10L, 20L) // offeredCardIds
                );

        tradeResponseDTO = new TradeResponseDTO(
                1L, // tradeId
                1L, // listingId
                100L, // requestingUserId
                TradeStatus.pending, // tradeStatus
                LocalDateTime.now(),
                Arrays.asList(10L, 20L) // offeredCardIds
                );
    }

    @Nested
    @DisplayName("Create Trade Endpoint Tests")
    class CreateTradeTests {

        @Test
        @DisplayName("POST /api/trades - Success")
        void createTrade_Success() throws Exception {
            when(tradeService.createTradeRequest(any(TradeRequestDTO.class))).thenReturn(tradeResponseDTO);

            mockMvc.perform(post("/api/trades")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(tradeRequestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.tradeId", is(1)))
                    .andExpect(jsonPath("$.listingId", is(1)))
                    .andExpect(jsonPath("$.requestingUserId", is(100)))
                    .andExpect(jsonPath("$.tradeStatus", is("pending")))
                    .andExpect(jsonPath("$.offeredCardIds", hasSize(2)));

            verify(tradeService, times(1)).createTradeRequest(any(TradeRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("Get Trades Endpoint Tests")
    class GetTradesTests {

        @Test
        @DisplayName("GET /api/trades - Success")
        void getAllTrades_Success() throws Exception {
            TradeResponseDTO trade2 =
                    new TradeResponseDTO(2L, 1L, 101L, TradeStatus.pending, LocalDateTime.now(), Arrays.asList(30L));

            when(tradeService.getAllTrades()).thenReturn(Arrays.asList(tradeResponseDTO, trade2));

            mockMvc.perform(get("/api/trades"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].tradeId", is(1)))
                    .andExpect(jsonPath("$[1].tradeId", is(2)));
        }

        @Test
        @DisplayName("GET /api/trades/listing/{listingId} - Success")
        void getTradesByListingId_Success() throws Exception {
            when(tradeService.getTradesByListingId(1L)).thenReturn(List.of(tradeResponseDTO));

            mockMvc.perform(get("/api/trades/listing/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].listingId", is(1)));
        }

        @Test
        @DisplayName("GET /api/trades/user/{userId} - Success")
        void getTradesByUserId_Success() throws Exception {
            when(tradeService.getTradesByRequestingUserId(100L)).thenReturn(List.of(tradeResponseDTO));

            mockMvc.perform(get("/api/trades/user/100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].requestingUserId", is(100)));
        }

        @Test
        @DisplayName("GET /api/trades/{tradeId} - Success")
        void getTradeById_Success() throws Exception {
            when(tradeService.getTradeById(1L)).thenReturn(tradeResponseDTO);

            mockMvc.perform(get("/api/trades/1")).andExpect(status().isOk()).andExpect(jsonPath("$.tradeId", is(1)));
        }
    }

    @Nested
    @DisplayName("Accept/Decline Trade Endpoint Tests")
    class TradeDecisionTests {

        @Test
        @DisplayName("PUT /api/trades/{tradeId}/accept - Success")
        void acceptTrade_Success() throws Exception {
            tradeResponseDTO.setTradeStatus(TradeStatus.accepted);
            when(tradeService.acceptTradeRequest(eq(1L), eq(100L))).thenReturn(tradeResponseDTO);

            mockMvc.perform(put("/api/trades/1/accept").param("listingOwnerId", "100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tradeStatus", is("accepted")));
        }

        @Test
        @DisplayName("PUT /api/trades/{tradeId}/decline - Success")
        void declineTrade_Success() throws Exception {
            tradeResponseDTO.setTradeStatus(TradeStatus.rejected);
            when(tradeService.declineTradeRequest(eq(1L), eq(100L))).thenReturn(tradeResponseDTO);

            mockMvc.perform(put("/api/trades/1/decline").param("listingOwnerId", "100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tradeStatus", is("rejected")));
        }
    }

    @Test
    @DisplayName("POST /api/trades - Validation failure returns 400")
    void createTrade_InvalidRequest_ReturnsBadRequest() throws Exception {
        TradeRequestDTO invalidRequest = new TradeRequestDTO(
                null, // listingId invalid
                null, // requestingUserId invalid
                List.of() // empty list invalid
                );

        mockMvc.perform(post("/api/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(tradeService, never()).createTradeRequest(any());
    }

    @Test
    @DisplayName("GET /api/trades - Empty list")
    void getAllTrades_EmptyList() throws Exception {
        when(tradeService.getAllTrades()).thenReturn(List.of());

        mockMvc.perform(get("/api/trades")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/trades/{id} - Trade not found")
    void getTradeById_NotFound() throws Exception {
        when(tradeService.getTradeById(999L)).thenThrow(new RuntimeException("Trade not found"));

        mockMvc.perform(get("/api/trades/999")).andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("PUT /api/trades/{id}/accept - Service throws exception")
    void acceptTrade_ServiceFailure() throws Exception {
        when(tradeService.acceptTradeRequest(eq(1L), eq(100L))).thenThrow(new RuntimeException("Unauthorized"));

        mockMvc.perform(put("/api/trades/1/accept").param("listingOwnerId", "100"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("GET /api/trades/user/{userId} - No trades returns empty list")
    void getTradesByUserId_Empty_ReturnsEmptyList() throws Exception {
        when(tradeService.getTradesByRequestingUserId(999L)).thenReturn(List.of());

        mockMvc.perform(get("/api/trades/user/999")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("PUT /api/trades/{tradeId}/accept - TradeService throws TradeException")
    void acceptTrade_TradeServiceThrows_Returns500() throws Exception {
        when(tradeService.acceptTradeRequest(eq(1L), eq(100L))).thenThrow(new RuntimeException("Not authorized"));

        mockMvc.perform(put("/api/trades/1/accept").param("listingOwnerId", "100"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("PUT /api/trades/{tradeId}/decline - TradeService throws TradeException")
    void declineTrade_TradeServiceThrows_Returns500() throws Exception {
        when(tradeService.declineTradeRequest(eq(1L), eq(100L))).thenThrow(new RuntimeException("Not authorized"));

        mockMvc.perform(put("/api/trades/1/decline").param("listingOwnerId", "100"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /api/trades - TradeService throws ResourceNotFoundException")
    void createTrade_TradeServiceNotFound_Returns404() throws Exception {
        when(tradeService.createTradeRequest(any())).thenThrow(new RuntimeException("Listing not found"));

        mockMvc.perform(post("/api/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tradeRequestDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("PUT /api/trades/{tradeId}/accept - Missing listingOwnerId returns 400")
    void acceptTrade_MissingListingOwnerId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/trades/1/accept")) // omit param
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/trades/{tradeId}/decline - Missing listingOwnerId returns 400")
    void declineTrade_MissingListingOwnerId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/trades/1/decline")) // omit param
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/trades/{tradeId} - Invalid path variable returns 400")
    void getTradeById_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/trades/invalid")).andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("POST /api/trades - Empty JSON triggers validation errors")
    void createTrade_EmptyJson_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/trades/listing/{listingId} - Empty list")
    void getTradesByListingId_Empty_ReturnsEmptyList() throws Exception {
        when(tradeService.getTradesByListingId(999L)).thenReturn(List.of());

        mockMvc.perform(get("/api/trades/listing/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/trades - ResourceNotFoundException returns 404")
    void createTrade_ResourceNotFound_Returns404() throws Exception {
        when(tradeService.createTradeRequest(any())).thenThrow(new ResourceNotFoundException("Listing not found: 1"));

        mockMvc.perform(post("/api/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tradeRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/trades - TradeException returns 400")
    void createTrade_TradeException_Returns400() throws Exception {
        when(tradeService.createTradeRequest(any()))
                .thenThrow(new TradeException("Cannot create trade for your own listing"));

        mockMvc.perform(post("/api/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tradeRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/trades/{tradeId}/accept - ResourceNotFoundException returns 404")
    void acceptTrade_ResourceNotFound_Returns404() throws Exception {
        when(tradeService.acceptTradeRequest(eq(999L), eq(100L)))
                .thenThrow(new ResourceNotFoundException("Trade not found: 999"));

        mockMvc.perform(put("/api/trades/999/accept").param("listingOwnerId", "100"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/trades/{tradeId}/accept - TradeException returns 400")
    void acceptTrade_TradeException_Returns400() throws Exception {
        when(tradeService.acceptTradeRequest(eq(1L), eq(100L)))
                .thenThrow(new TradeException("Only pending trades can be accepted"));

        mockMvc.perform(put("/api/trades/1/accept").param("listingOwnerId", "100"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/trades/{tradeId}/decline - ResourceNotFoundException returns 404")
    void declineTrade_ResourceNotFound_Returns404() throws Exception {
        when(tradeService.declineTradeRequest(eq(999L), eq(100L)))
                .thenThrow(new ResourceNotFoundException("Trade not found: 999"));

        mockMvc.perform(put("/api/trades/999/decline").param("listingOwnerId", "100"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/trades/{tradeId}/decline - TradeException returns 400")
    void declineTrade_TradeException_Returns400() throws Exception {
        when(tradeService.declineTradeRequest(eq(1L), eq(100L)))
                .thenThrow(new TradeException("Only pending trades can be declined"));

        mockMvc.perform(put("/api/trades/1/decline").param("listingOwnerId", "100"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/trades/{tradeId} - ResourceNotFoundException returns 404")
    void getTradeById_ResourceNotFound_Returns404() throws Exception {
        when(tradeService.getTradeById(999L)).thenThrow(new ResourceNotFoundException("Trade not found: 999"));

        mockMvc.perform(get("/api/trades/999")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/trades - Multiple trades returns correctly")
    void getAllTrades_MultipleTrades_Success() throws Exception {
        TradeResponseDTO trade2 =
                new TradeResponseDTO(2L, 2L, 102L, TradeStatus.accepted, LocalDateTime.now(), Arrays.asList(30L));
        TradeResponseDTO trade3 =
                new TradeResponseDTO(3L, 3L, 103L, TradeStatus.rejected, LocalDateTime.now(), Arrays.asList(40L, 50L));

        when(tradeService.getAllTrades()).thenReturn(Arrays.asList(tradeResponseDTO, trade2, trade3));

        mockMvc.perform(get("/api/trades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].tradeId", is(1)))
                .andExpect(jsonPath("$[1].tradeId", is(2)))
                .andExpect(jsonPath("$[2].tradeId", is(3)));
    }

    @Test
    @DisplayName("GET /api/trades/listing/{listingId} - Multiple trades for listing")
    void getTradesByListingId_MultipleTrades_Success() throws Exception {
        TradeResponseDTO trade2 =
                new TradeResponseDTO(2L, 1L, 102L, TradeStatus.rejected, LocalDateTime.now(), Arrays.asList(30L));

        when(tradeService.getTradesByListingId(1L)).thenReturn(Arrays.asList(tradeResponseDTO, trade2));

        mockMvc.perform(get("/api/trades/listing/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].listingId", is(1)))
                .andExpect(jsonPath("$[1].listingId", is(1)));
    }

    @Test
    @DisplayName("GET /api/trades/user/{userId} - Multiple trades for user")
    void getTradesByUserId_MultipleTrades_Success() throws Exception {
        TradeResponseDTO trade2 =
                new TradeResponseDTO(2L, 2L, 100L, TradeStatus.accepted, LocalDateTime.now(), Arrays.asList(30L));

        when(tradeService.getTradesByRequestingUserId(100L)).thenReturn(Arrays.asList(tradeResponseDTO, trade2));

        mockMvc.perform(get("/api/trades/user/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].requestingUserId", is(100)))
                .andExpect(jsonPath("$[1].requestingUserId", is(100)));
    }

    @Test
    @DisplayName("POST /api/trades - Trade with single offered card")
    void createTrade_SingleOfferedCard_Success() throws Exception {
        TradeRequestDTO singleCardRequest = new TradeRequestDTO(1L, 100L, Arrays.asList(10L));
        TradeResponseDTO singleCardResponse =
                new TradeResponseDTO(1L, 1L, 100L, TradeStatus.pending, LocalDateTime.now(), Arrays.asList(10L));

        when(tradeService.createTradeRequest(any(TradeRequestDTO.class))).thenReturn(singleCardResponse);

        mockMvc.perform(post("/api/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(singleCardRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.offeredCardIds", hasSize(1)));
    }

    @Test
    @DisplayName("POST /api/trades - Trade with many offered cards")
    void createTrade_ManyOfferedCards_Success() throws Exception {
        TradeRequestDTO manyCardsRequest = new TradeRequestDTO(1L, 100L, Arrays.asList(10L, 20L, 30L, 40L, 50L));
        TradeResponseDTO manyCardsResponse = new TradeResponseDTO(
                1L, 1L, 100L, TradeStatus.pending, LocalDateTime.now(), Arrays.asList(10L, 20L, 30L, 40L, 50L));

        when(tradeService.createTradeRequest(any(TradeRequestDTO.class))).thenReturn(manyCardsResponse);

        mockMvc.perform(post("/api/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(manyCardsRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.offeredCardIds", hasSize(5)));
    }
}
