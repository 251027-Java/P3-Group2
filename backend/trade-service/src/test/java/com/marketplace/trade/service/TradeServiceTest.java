// Generated with assistance from Cursor & Claude Opus 4.5
// Reviewed and modified by Matt Selle & Marcus Wright
package com.marketplace.trade.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.marketplace.trade.client.ListingServiceClient;
import com.marketplace.trade.client.UserServiceClient;
import com.marketplace.trade.client.dto.ListingResponse;
import com.marketplace.trade.client.dto.ListingStatus;
import com.marketplace.trade.client.dto.UserResponse;
import com.marketplace.trade.dto.TradeRequestDTO;
import com.marketplace.trade.dto.TradeResponseDTO;
import com.marketplace.trade.exception.ResourceNotFoundException;
import com.marketplace.trade.exception.TradeException;
import com.marketplace.trade.model.Trade;
import com.marketplace.trade.model.Trade.TradeStatus;
import com.marketplace.trade.model.TradeOfferedCard;
import com.marketplace.trade.repository.TradeOfferedCardRepository;
import com.marketplace.trade.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Unit tests for TradeService
 */
@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private TradeOfferedCardRepository tradeOfferedCardRepository;

    @Mock
    private ListingServiceClient listingServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private TradeService tradeService;

    private TradeRequestDTO tradeRequestDTO;
    private ListingResponse listingResponse;
    private UserResponse userResponse;
    private Trade trade;

    @BeforeEach
    void setUp() {
        tradeRequestDTO = new TradeRequestDTO();
        tradeRequestDTO.setListingId(1L);
        tradeRequestDTO.setRequestingUserId(2L);
        tradeRequestDTO.setOfferedCardIds(Arrays.asList(10L, 11L));

        listingResponse = new ListingResponse();
        listingResponse.setListingId(1L);
        listingResponse.setOwnerUserId(1L);
        listingResponse.setCardId(5L);
        listingResponse.setListingStatus(ListingStatus.ACTIVE);

        userResponse = new UserResponse();
        userResponse.setUserId(2L);
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");

        trade = new Trade();
        trade.setTradeId(1L);
        trade.setListingId(1L);
        trade.setRequestingUserId(2L);
        trade.setListingOwnerUserId(1L);
        trade.setTradeStatus(TradeStatus.pending);
        trade.setCreatedAt(LocalDateTime.now());

        TradeOfferedCard card1 = new TradeOfferedCard();
        card1.setCardId(10L);
        card1.setTrade(trade);

        TradeOfferedCard card2 = new TradeOfferedCard();
        card2.setCardId(11L);
        card2.setTrade(trade);

        trade.setOfferedCards(Arrays.asList(card1, card2));
    }

    @Test
    void testCreateTradeRequest_Success() {
        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);
        when(userServiceClient.getUser(2L)).thenReturn(userResponse);
        when(tradeRepository.findPendingTradeByListingAndUser(1L, 2L)).thenReturn(Optional.empty());
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);
        when(tradeOfferedCardRepository.saveAll(anyList())).thenReturn(Arrays.asList());

        TradeResponseDTO result = tradeService.createTradeRequest(tradeRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getTradeId());
        assertEquals(1L, result.getListingId());
        assertEquals(2L, result.getRequestingUserId());
        assertEquals(TradeStatus.pending, result.getTradeStatus());
        assertEquals(2, result.getOfferedCardIds().size());

        verify(tradeRepository, times(1)).save(any(Trade.class));
        verify(tradeOfferedCardRepository, times(1)).saveAll(anyList());
        verify(kafkaTemplate, times(1)).send(anyString(), any());
    }

    @Test
    void testCreateTradeRequest_ListingNotFound() {
        when(listingServiceClient.getListing(1L)).thenThrow(new RuntimeException("Not found"));

        assertThrows(ResourceNotFoundException.class, () -> {
            tradeService.createTradeRequest(tradeRequestDTO);
        });

        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    void testCreateTradeRequest_InactiveListing() {
        listingResponse.setListingStatus(ListingStatus.COMPLETED);
        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);

        assertThrows(TradeException.class, () -> {
            tradeService.createTradeRequest(tradeRequestDTO);
        });

        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    void testCreateTradeRequest_OwnListing() {
        tradeRequestDTO.setRequestingUserId(1L);
        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);

        assertThrows(TradeException.class, () -> {
            tradeService.createTradeRequest(tradeRequestDTO);
        });

        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    void testCreateTradeRequest_PendingTradeExists() {
        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);
        when(userServiceClient.getUser(2L)).thenReturn(userResponse);
        when(tradeRepository.findPendingTradeByListingAndUser(1L, 2L)).thenReturn(Optional.of(trade));

        assertThrows(TradeException.class, () -> {
            tradeService.createTradeRequest(tradeRequestDTO);
        });

        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    void testAcceptTradeRequest_Success() {
        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.of(trade));
        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);
        when(tradeRepository.findByListingIdAndTradeStatus(1L, TradeStatus.pending))
                .thenReturn(Arrays.asList(trade));
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        TradeResponseDTO result = tradeService.acceptTradeRequest(1L, 1L);

        assertNotNull(result);
        assertEquals(TradeStatus.accepted, result.getTradeStatus());

        verify(tradeRepository, times(1)).save(any(Trade.class));
        verify(listingServiceClient, times(1)).updateListingStatusToComplete(eq(1L));
        verify(kafkaTemplate, times(1)).send(anyString(), any());
    }

    @Test
    void testAcceptTradeRequest_TradeNotFound() {
        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            tradeService.acceptTradeRequest(1L, 1L);
        });

        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    void testAcceptTradeRequest_NotPending() {
        trade.setTradeStatus(TradeStatus.accepted);
        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.of(trade));

        assertThrows(TradeException.class, () -> {
            tradeService.acceptTradeRequest(1L, 1L);
        });

        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    void testAcceptTradeRequest_Unauthorized() {
        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.of(trade));
        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);

        assertThrows(TradeException.class, () -> {
            tradeService.acceptTradeRequest(1L, 999L);
        });

        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    void testDeclineTradeRequest_Success() {
        when(tradeRepository.findById(1L)).thenReturn(Optional.of(trade));
        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        TradeResponseDTO result = tradeService.declineTradeRequest(1L, 1L);

        assertNotNull(result);
        assertEquals(TradeStatus.rejected, result.getTradeStatus());

        verify(tradeRepository, times(1)).save(any(Trade.class));
        verify(kafkaTemplate, times(1)).send(anyString(), any());
    }

    @Test
    void testGetTradeById_Success() {
        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.of(trade));

        TradeResponseDTO result = tradeService.getTradeById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getTradeId());
        assertEquals(2, result.getOfferedCardIds().size());
    }

    @Test
    void testGetTradeById_NotFound() {
        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            tradeService.getTradeById(1L);
        });
    }

    @Test
    void testGetTradesByListingId() {
        when(tradeRepository.findByListingId(1L)).thenReturn(Arrays.asList(trade));

        List<TradeResponseDTO> results = tradeService.getTradesByListingId(1L);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getListingId());
    }

    @Test
    void testGetTradesByRequestingUserId() {
        when(tradeRepository.findByRequestingUserId(2L)).thenReturn(Arrays.asList(trade));

        List<TradeResponseDTO> results = tradeService.getTradesByRequestingUserId(2L);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(2L, results.get(0).getRequestingUserId());
    }

    @Test
    void testCreateTradeRequest_NoOfferedCards() {
        tradeRequestDTO.setOfferedCardIds(List.of()); // no cards offered

        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);
        when(userServiceClient.getUser(2L)).thenReturn(userResponse);
        when(tradeRepository.findPendingTradeByListingAndUser(1L, 2L)).thenReturn(Optional.empty());

        // MOCK the repository to return a Trade object
        Trade savedTrade = new Trade();
        savedTrade.setTradeId(1L);
        savedTrade.setListingId(tradeRequestDTO.getListingId());
        savedTrade.setRequestingUserId(tradeRequestDTO.getRequestingUserId());
        savedTrade.setListingOwnerUserId(1L);
        savedTrade.setTradeStatus(TradeStatus.pending);
        savedTrade.setOfferedCards(List.of()); // no offered cards

        when(tradeRepository.save(any(Trade.class))).thenReturn(savedTrade);
        when(tradeOfferedCardRepository.saveAll(anyList())).thenReturn(List.of()); // empty list

        // call service
        TradeResponseDTO result = tradeService.createTradeRequest(tradeRequestDTO);

        assertNotNull(result);
        assertEquals(0, result.getOfferedCardIds().size());

        verify(tradeOfferedCardRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testAcceptTradeRequest_OtherPendingTradesRejected() {
        Trade anotherTrade = new Trade();
        anotherTrade.setTradeId(2L);
        anotherTrade.setListingId(1L);
        anotherTrade.setListingOwnerUserId(1L);
        anotherTrade.setTradeStatus(TradeStatus.pending);
        anotherTrade.setRequestingUserId(3L);

        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.of(trade));
        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);
        when(tradeRepository.findByListingIdAndTradeStatus(1L, TradeStatus.pending))
                .thenReturn(Arrays.asList(trade, anotherTrade));
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        TradeResponseDTO result = tradeService.acceptTradeRequest(1L, 1L);

        assertNotNull(result);
        assertEquals(TradeStatus.accepted, result.getTradeStatus());
        // Ensure the other pending trade is cancelled
        assertEquals(TradeStatus.rejected, anotherTrade.getTradeStatus());

        verify(tradeRepository, times(2)).save(any(Trade.class)); // accepted + cancelled
        verify(listingServiceClient).updateListingStatusToComplete(eq(1L));
    }

    @Test
    void testDeclineTradeRequest_TradeNotFound() {
        when(tradeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> tradeService.declineTradeRequest(1L, 1L));
        verify(tradeRepository, never()).save(any());
    }

    @Test
    void getAllTrades_shouldReturnAllTrades() {
        when(tradeRepository.findAll()).thenReturn(List.of(trade));

        List<TradeResponseDTO> results = tradeService.getAllTrades();

        assertEquals(1, results.size());
    }

    @Test
    void testGetTradesByListingId_Empty() {
        when(tradeRepository.findByListingId(999L)).thenReturn(List.of());
        List<TradeResponseDTO> results = tradeService.getTradesByListingId(999L);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetTradesByRequestingUserId_Empty() {
        when(tradeRepository.findByRequestingUserId(999L)).thenReturn(List.of());
        List<TradeResponseDTO> results = tradeService.getTradesByRequestingUserId(999L);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testAcceptTradeRequest_WithNullOfferedCards() {
        trade.setOfferedCards(null);
        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.of(trade));
        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);
        when(tradeRepository.findByListingIdAndTradeStatus(1L, TradeStatus.pending))
                .thenReturn(List.of(trade));
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        TradeResponseDTO result = tradeService.acceptTradeRequest(1L, 1L);
        assertNotNull(result);
        assertEquals(TradeStatus.accepted, result.getTradeStatus());
    }

    @Test
    void testDeclineTradeRequest_Unauthorized() {
        trade.setRequestingUserId(2L); // owner is 1L in listing
        when(tradeRepository.findById(1L)).thenReturn(Optional.of(trade));
        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);

        assertThrows(TradeException.class, () -> tradeService.declineTradeRequest(1L, 999L));
        verify(tradeRepository, never()).save(any());
    }

    @Test
    void testAcceptTradeRequest_MultipleOtherPendingTrades() {
        Trade another1 = new Trade();
        another1.setTradeId(2L);
        another1.setTradeStatus(TradeStatus.pending);
        another1.setListingId(1L);
        another1.setListingOwnerUserId(1L);
        another1.setRequestingUserId(3L);

        Trade another2 = new Trade();
        another2.setTradeId(3L);
        another2.setTradeStatus(TradeStatus.pending);
        another2.setListingId(1L);
        another2.setListingOwnerUserId(1L);
        another2.setRequestingUserId(4L);

        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.of(trade));
        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);
        when(tradeRepository.findByListingIdAndTradeStatus(1L, TradeStatus.pending))
                .thenReturn(Arrays.asList(trade, another1, another2));
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        TradeResponseDTO result = tradeService.acceptTradeRequest(1L, 1L);

        assertEquals(TradeStatus.accepted, result.getTradeStatus());
        assertEquals(TradeStatus.rejected, another1.getTradeStatus());
        assertEquals(TradeStatus.rejected, another2.getTradeStatus());

        verify(tradeRepository, times(3)).save(any()); // accepted + 2 cancelled
    }

    @Test
    void declineTradeRequest_whenKafkaFails_shouldStillSucceed() {
        Trade trade = new Trade();
        trade.setTradeId(1L);
        trade.setListingId(10L);
        trade.setRequestingUserId(20L);
        trade.setListingOwnerUserId(99L);
        trade.setTradeStatus(Trade.TradeStatus.pending);

        ListingResponse listing = new ListingResponse();
        listing.setOwnerUserId(99L);

        when(tradeRepository.findById(1L)).thenReturn(Optional.of(trade));
        when(listingServiceClient.getListing(10L)).thenReturn(listing);

        when(tradeRepository.save(any(Trade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        doThrow(new RuntimeException("Kafka down")).when(kafkaTemplate).send(anyString(), any());

        TradeResponseDTO result = tradeService.declineTradeRequest(1L, 99L);

        assertEquals(Trade.TradeStatus.rejected, result.getTradeStatus());
    }

    @Test
    void getTradeById_whenOfferedCardsNull_shouldMapWithoutError() {
        Trade trade = new Trade();
        trade.setTradeId(1L);
        trade.setListingId(2L);
        trade.setRequestingUserId(3L);
        trade.setListingOwnerUserId(1L);
        trade.setTradeStatus(Trade.TradeStatus.pending);
        trade.setOfferedCards(null);

        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.of(trade));

        TradeResponseDTO dto = tradeService.getTradeById(1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getTradeId());
        assertNull(dto.getOfferedCardIds());
    }

    @Test
    void acceptTradeRequest_shouldRejectOtherPendingTrades() {
        Trade accepted = new Trade();
        accepted.setTradeId(1L);
        accepted.setListingId(50L);
        accepted.setRequestingUserId(60L);
        accepted.setListingOwnerUserId(99L);
        accepted.setTradeStatus(Trade.TradeStatus.pending);

        Trade other = new Trade();
        other.setTradeId(2L);
        other.setListingId(50L);
        other.setListingOwnerUserId(99L);
        other.setTradeStatus(Trade.TradeStatus.pending);

        ListingResponse listing = new ListingResponse();
        listing.setOwnerUserId(99L);

        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.of(accepted));
        when(listingServiceClient.getListing(50L)).thenReturn(listing);
        when(tradeRepository.findByListingIdAndTradeStatus(50L, Trade.TradeStatus.pending))
                .thenReturn(List.of(accepted, other));

        when(tradeRepository.save(any(Trade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        tradeService.acceptTradeRequest(1L, 99L);

        verify(tradeRepository).save(other);
    }

    @Test
    void acceptTradeRequest_whenListingNotFound_shouldThrow() {
        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.of(trade));
        when(listingServiceClient.getListing(anyLong())).thenThrow(new RuntimeException("down"));

        assertThrows(ResourceNotFoundException.class, () -> tradeService.acceptTradeRequest(1L, 1L));
    }

    @Test
    void declineTradeRequest_whenListingNotFound_shouldThrow() {
        when(tradeRepository.findById(1L)).thenReturn(Optional.of(trade));
        when(listingServiceClient.getListing(anyLong())).thenThrow(new RuntimeException("down"));

        assertThrows(ResourceNotFoundException.class, () -> tradeService.declineTradeRequest(1L, 1L));
    }

    @Test
    void createTradeRequest_whenUserNotFound_shouldThrow() {
        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);
        when(userServiceClient.getUser(anyLong())).thenThrow(new RuntimeException("user down"));

        assertThrows(ResourceNotFoundException.class, () -> tradeService.createTradeRequest(tradeRequestDTO));
    }

    @Test
    void getTradeById_withNullOfferedCards_shouldSucceed() {
        trade.setOfferedCards(null);
        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.of(trade));

        TradeResponseDTO dto = tradeService.getTradeById(1L);

        assertNotNull(dto);
        assertNull(dto.getOfferedCardIds());
    }

    @Test
    void acceptTradeRequest_whenKafkaFails_shouldStillSucceed() {
        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.of(trade));
        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);
        when(tradeRepository.findByListingIdAndTradeStatus(anyLong(), any())).thenReturn(List.of(trade));
        when(tradeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        doThrow(new RuntimeException("Kafka down")).when(kafkaTemplate).send(anyString(), any());

        TradeResponseDTO result = tradeService.acceptTradeRequest(1L, 1L);

        assertEquals(TradeStatus.accepted, result.getTradeStatus());
    }

    @Test
    void testDeclineTradeRequest_NotPending() {
        trade.setTradeStatus(TradeStatus.accepted);
        when(tradeRepository.findById(1L)).thenReturn(Optional.of(trade));

        assertThrows(TradeException.class, () -> {
            tradeService.declineTradeRequest(1L, 1L);
        });

        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    void testDeclineTradeRequest_AlreadyRejected() {
        trade.setTradeStatus(TradeStatus.rejected);
        when(tradeRepository.findById(1L)).thenReturn(Optional.of(trade));

        assertThrows(TradeException.class, () -> {
            tradeService.declineTradeRequest(1L, 1L);
        });

        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    void testCreateTradeRequest_KafkaFails_ShouldStillSucceed() {
        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);
        when(userServiceClient.getUser(2L)).thenReturn(userResponse);
        when(tradeRepository.findPendingTradeByListingAndUser(1L, 2L)).thenReturn(Optional.empty());
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);
        when(tradeOfferedCardRepository.saveAll(anyList())).thenReturn(Arrays.asList());

        doThrow(new RuntimeException("Kafka down")).when(kafkaTemplate).send(anyString(), any());

        TradeResponseDTO result = tradeService.createTradeRequest(tradeRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getTradeId());
        verify(tradeRepository, times(1)).save(any(Trade.class));
    }

    @Test
    void testAcceptTradeRequest_UpdateListingStatusFails_ShouldStillSucceed() {
        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.of(trade));
        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);
        when(tradeRepository.findByListingIdAndTradeStatus(1L, TradeStatus.pending))
                .thenReturn(Arrays.asList(trade));
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        doThrow(new RuntimeException("Listing service down"))
                .when(listingServiceClient)
                .updateListingStatusToComplete(eq(1L));

        TradeResponseDTO result = tradeService.acceptTradeRequest(1L, 1L);

        assertNotNull(result);
        assertEquals(TradeStatus.accepted, result.getTradeStatus());
    }

    @Test
    void testAcceptTradeRequest_NoOtherPendingTrades() {
        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.of(trade));
        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);
        when(tradeRepository.findByListingIdAndTradeStatus(1L, TradeStatus.pending))
                .thenReturn(Arrays.asList(trade)); // only the accepted trade itself
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        TradeResponseDTO result = tradeService.acceptTradeRequest(1L, 1L);

        assertNotNull(result);
        assertEquals(TradeStatus.accepted, result.getTradeStatus());
        verify(tradeRepository, times(1)).save(any(Trade.class)); // only the accepted trade
    }

    @Test
    void testGetAllTrades_EmptyList() {
        when(tradeRepository.findAll()).thenReturn(List.of());

        List<TradeResponseDTO> results = tradeService.getAllTrades();

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetAllTrades_MultipleTrades() {
        Trade trade2 = new Trade();
        trade2.setTradeId(2L);
        trade2.setListingId(2L);
        trade2.setRequestingUserId(3L);
        trade2.setListingOwnerUserId(1L);
        trade2.setTradeStatus(TradeStatus.accepted);

        when(tradeRepository.findAll()).thenReturn(Arrays.asList(trade, trade2));

        List<TradeResponseDTO> results = tradeService.getAllTrades();

        assertEquals(2, results.size());
    }

    @Test
    void testCreateTradeRequest_InactiveListingCancelled() {
        listingResponse.setListingStatus(ListingStatus.CANCELLED);
        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);

        assertThrows(TradeException.class, () -> {
            tradeService.createTradeRequest(tradeRequestDTO);
        });
    }

    @Test
    void testGetTradesByListingId_MultipleTrades() {
        Trade trade2 = new Trade();
        trade2.setTradeId(2L);
        trade2.setListingId(1L);
        trade2.setRequestingUserId(3L);
        trade2.setListingOwnerUserId(1L);
        trade2.setTradeStatus(TradeStatus.rejected);

        when(tradeRepository.findByListingId(1L)).thenReturn(Arrays.asList(trade, trade2));

        List<TradeResponseDTO> results = tradeService.getTradesByListingId(1L);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(t -> t.getListingId().equals(1L)));
    }

    @Test
    void testGetTradesByRequestingUserId_MultipleTrades() {
        Trade trade2 = new Trade();
        trade2.setTradeId(2L);
        trade2.setListingId(2L);
        trade2.setRequestingUserId(2L);
        trade2.setListingOwnerUserId(1L);
        trade2.setTradeStatus(TradeStatus.accepted);

        when(tradeRepository.findByRequestingUserId(2L)).thenReturn(Arrays.asList(trade, trade2));

        List<TradeResponseDTO> results = tradeService.getTradesByRequestingUserId(2L);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(t -> t.getRequestingUserId().equals(2L)));
    }

    @Test
    void testAcceptTradeRequest_TradeStatusCancelled() {
        trade.setTradeStatus(TradeStatus.cancelled);
        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.of(trade));

        assertThrows(TradeException.class, () -> {
            tradeService.acceptTradeRequest(1L, 1L);
        });

        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    void testAcceptTradeRequest_TradeStatusRejected() {
        trade.setTradeStatus(TradeStatus.rejected);
        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.of(trade));

        assertThrows(TradeException.class, () -> {
            tradeService.acceptTradeRequest(1L, 1L);
        });

        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    void testDeclineTradeRequest_TradeStatusCancelled() {
        trade.setTradeStatus(TradeStatus.cancelled);
        when(tradeRepository.findById(1L)).thenReturn(Optional.of(trade));

        assertThrows(TradeException.class, () -> {
            tradeService.declineTradeRequest(1L, 1L);
        });

        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    void testGetTradeById_WithOfferedCards() {
        TradeOfferedCard card1 = new TradeOfferedCard();
        card1.setCardId(10L);
        card1.setTrade(trade);

        TradeOfferedCard card2 = new TradeOfferedCard();
        card2.setCardId(11L);
        card2.setTrade(trade);

        trade.setOfferedCards(Arrays.asList(card1, card2));
        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.of(trade));

        TradeResponseDTO result = tradeService.getTradeById(1L);

        assertNotNull(result);
        assertEquals(2, result.getOfferedCardIds().size());
        assertTrue(result.getOfferedCardIds().contains(10L));
        assertTrue(result.getOfferedCardIds().contains(11L));
    }

    @Test
    void testCreateTradeRequest_WithSingleOfferedCard() {
        tradeRequestDTO.setOfferedCardIds(Arrays.asList(10L));

        Trade savedTrade = new Trade();
        savedTrade.setTradeId(1L);
        savedTrade.setListingId(1L);
        savedTrade.setRequestingUserId(2L);
        savedTrade.setListingOwnerUserId(1L);
        savedTrade.setTradeStatus(TradeStatus.pending);

        TradeOfferedCard card = new TradeOfferedCard();
        card.setCardId(10L);
        card.setTrade(savedTrade);
        savedTrade.setOfferedCards(Arrays.asList(card));

        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);
        when(userServiceClient.getUser(2L)).thenReturn(userResponse);
        when(tradeRepository.findPendingTradeByListingAndUser(1L, 2L)).thenReturn(Optional.empty());
        when(tradeRepository.save(any(Trade.class))).thenReturn(savedTrade);
        when(tradeOfferedCardRepository.saveAll(anyList())).thenReturn(Arrays.asList(card));

        TradeResponseDTO result = tradeService.createTradeRequest(tradeRequestDTO);

        assertNotNull(result);
        assertEquals(1, result.getOfferedCardIds().size());
    }

    @Test
    void testCreateTradeRequest_WithManyOfferedCards() {
        tradeRequestDTO.setOfferedCardIds(Arrays.asList(10L, 11L, 12L, 13L, 14L));

        Trade savedTrade = new Trade();
        savedTrade.setTradeId(1L);
        savedTrade.setListingId(1L);
        savedTrade.setRequestingUserId(2L);
        savedTrade.setListingOwnerUserId(1L);
        savedTrade.setTradeStatus(TradeStatus.pending);
        savedTrade.setOfferedCards(Arrays.asList());

        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);
        when(userServiceClient.getUser(2L)).thenReturn(userResponse);
        when(tradeRepository.findPendingTradeByListingAndUser(1L, 2L)).thenReturn(Optional.empty());
        when(tradeRepository.save(any(Trade.class))).thenReturn(savedTrade);
        when(tradeOfferedCardRepository.saveAll(anyList())).thenReturn(Arrays.asList());

        TradeResponseDTO result = tradeService.createTradeRequest(tradeRequestDTO);

        assertNotNull(result);
        verify(tradeOfferedCardRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testDeclineTradeRequest_ByListingOwner() {
        // Trade was made by user 2, listing owned by user 1
        trade.setRequestingUserId(2L);
        when(tradeRepository.findById(1L)).thenReturn(Optional.of(trade));
        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        TradeResponseDTO result = tradeService.declineTradeRequest(1L, 1L);

        assertNotNull(result);
        assertEquals(TradeStatus.rejected, result.getTradeStatus());
    }

    @Test
    void testAcceptTradeRequest_EmptyPendingTradesList() {
        when(tradeRepository.findByIdWithOfferedCards(1L)).thenReturn(Optional.of(trade));
        when(listingServiceClient.getListing(1L)).thenReturn(listingResponse);
        when(tradeRepository.findByListingIdAndTradeStatus(1L, TradeStatus.pending))
                .thenReturn(Arrays.asList()); // empty list, though unusual
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        TradeResponseDTO result = tradeService.acceptTradeRequest(1L, 1L);

        assertNotNull(result);
        assertEquals(TradeStatus.accepted, result.getTradeStatus());
    }
}
