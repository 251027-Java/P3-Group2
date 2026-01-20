// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.service;

import com.marketplace.trade.client.ListingServiceClient;
import com.marketplace.trade.client.UserServiceClient;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    private ListingServiceClient.ListingResponse listingResponse;
    private UserServiceClient.UserResponse userResponse;
    private Trade trade;
    
    @BeforeEach
    void setUp() {
        tradeRequestDTO = new TradeRequestDTO();
        tradeRequestDTO.setListingId(1L);
        tradeRequestDTO.setRequestingUserId(2L);
        tradeRequestDTO.setOfferedCardIds(Arrays.asList(10L, 11L));
        
        listingResponse = new ListingServiceClient.ListingResponse();
        listingResponse.setListingId(1L);
        listingResponse.setOwnerUserId(1L);
        listingResponse.setCardId(5L);
        listingResponse.setListingStatus("active");
        
        userResponse = new UserServiceClient.UserResponse();
        userResponse.setAppUserId(2L);
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");
        
        trade = new Trade();
        trade.setTradeId(1L);
        trade.setListingId(1L);
        trade.setRequestingUserId(2L);
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
        listingResponse.setListingStatus("completed");
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
        when(tradeRepository.findPendingTradeByListingAndUser(1L, 2L))
                .thenReturn(Optional.of(trade));
        
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
        verify(listingServiceClient, times(1)).updateListingStatus(eq(1L), any());
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
}
