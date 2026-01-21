// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.service;

import com.marketplace.trade.client.ListingServiceClient;
import com.marketplace.trade.client.UserServiceClient;
import com.marketplace.trade.client.dto.ListingResponse;
import com.marketplace.trade.dto.TradeRequestDTO;
import com.marketplace.trade.dto.TradeResponseDTO;
import com.marketplace.trade.exception.ResourceNotFoundException;
import com.marketplace.trade.exception.TradeException;
import com.marketplace.trade.model.Trade;
import com.marketplace.trade.model.Trade.TradeStatus;
import com.marketplace.trade.model.TradeOfferedCard;
import com.marketplace.trade.repository.TradeOfferedCardRepository;
import com.marketplace.trade.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for trade operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TradeService {

    private final TradeRepository tradeRepository;
    private final TradeOfferedCardRepository tradeOfferedCardRepository;
    private final ListingServiceClient listingServiceClient;
    private final UserServiceClient userServiceClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TRADE_EVENTS_TOPIC = "trade-events";

    /**
     * Create a new trade request
     */
    @Transactional
    public TradeResponseDTO createTradeRequest(TradeRequestDTO request) {
        log.info(
                "Creating trade request for listing: {} by user: {}",
                request.getListingId(),
                request.getRequestingUserId());

        // Validate listing exists and is active
        ListingResponse listing;
        try {
            listing = listingServiceClient.getListing(request.getListingId());
        } catch (Exception e) {
            log.error("Failed to fetch listing: {}", request.getListingId(), e);
            throw new ResourceNotFoundException("Listing not found: " + request.getListingId());
        }

        if (!"active".equalsIgnoreCase(listing.getListingStatus().getValue())) {
            throw new TradeException("Cannot create trade request for inactive listing");
        }

        // Validate user exists
        try {
            userServiceClient.getUser(request.getRequestingUserId());
        } catch (Exception e) {
            log.error("Failed to fetch user: {}", request.getRequestingUserId(), e);
            throw new ResourceNotFoundException("User not found: " + request.getRequestingUserId());
        }

        // Check if user is trying to trade their own listing
        if (listing.getOwnerUserId().equals(request.getRequestingUserId())) {
            throw new TradeException("Cannot create trade request for your own listing");
        }

        // Check if there's already a pending trade for this listing by this user
        tradeRepository
                .findPendingTradeByListingAndUser(request.getListingId(), request.getRequestingUserId())
                .ifPresent(existing -> {
                    throw new TradeException("Pending trade request already exists for this listing");
                });

        // Create trade
        Trade trade = new Trade();
        trade.setListingId(request.getListingId());
        trade.setRequestingUserId(request.getRequestingUserId());
        trade.setTradeStatus(TradeStatus.pending);

        Trade savedTrade = tradeRepository.save(trade);
        log.info("Trade created with ID: {}", savedTrade.getTradeId());

        // Save offered cards
        List<TradeOfferedCard> offeredCards = request.getOfferedCardIds().stream()
                .map(cardId -> {
                    TradeOfferedCard card = new TradeOfferedCard();
                    card.setTrade(savedTrade);
                    card.setCardId(cardId);
                    return card;
                })
                .collect(Collectors.toList());

        tradeOfferedCardRepository.saveAll(offeredCards);

        // Publish trade created event
        publishTradeEvent(
                "TRADE_CREATED", savedTrade.getTradeId(), request.getListingId(), request.getRequestingUserId());

        return mapToDTO(savedTrade);
    }

    /**
     * Accept a trade request
     */
    @Transactional
    public TradeResponseDTO acceptTradeRequest(Long tradeId, Long listingOwnerId) {
        log.info("Accepting trade request: {} by listing owner: {}", tradeId, listingOwnerId);

        Trade trade = tradeRepository
                .findByIdWithOfferedCards(tradeId)
                .orElseThrow(() -> new ResourceNotFoundException("Trade not found: " + tradeId));

        if (trade.getTradeStatus() != TradeStatus.pending) {
            throw new TradeException("Only pending trades can be accepted");
        }

        // Validate listing owner
        ListingResponse listing;
        try {
            listing = listingServiceClient.getListing(trade.getListingId());
        } catch (Exception e) {
            log.error("Failed to fetch listing: {}", trade.getListingId(), e);
            throw new ResourceNotFoundException("Listing not found: " + trade.getListingId());
        }

        if (!listing.getOwnerUserId().equals(listingOwnerId)) {
            throw new TradeException("Only the listing owner can accept trade requests");
        }

        // Update trade status
        trade.setTradeStatus(TradeStatus.accepted);
        Trade savedTrade = tradeRepository.save(trade);

        // Update listing status to completed
        try {
            listingServiceClient.updateListingStatusToComplete(trade.getListingId());
        } catch (Exception e) {
            log.error("Failed to update listing status", e);
        }

        // Reject all other pending trades for this listing
        List<Trade> otherPendingTrades =
                tradeRepository.findByListingIdAndTradeStatus(trade.getListingId(), TradeStatus.pending);
        otherPendingTrades.stream().filter(t -> !t.getTradeId().equals(tradeId)).forEach(t -> {
            t.setTradeStatus(TradeStatus.rejected);
            tradeRepository.save(t);
        });

        // Publish trade accepted event
        publishTradeEvent("TRADE_ACCEPTED", savedTrade.getTradeId(), trade.getListingId(), trade.getRequestingUserId());

        log.info("Trade request accepted: {}", tradeId);
        return mapToDTO(savedTrade);
    }

    /**
     * Decline a trade request
     */
    @Transactional
    public TradeResponseDTO declineTradeRequest(Long tradeId, Long listingOwnerId) {
        log.info("Declining trade request: {} by listing owner: {}", tradeId, listingOwnerId);

        Trade trade = tradeRepository
                .findById(tradeId)
                .orElseThrow(() -> new ResourceNotFoundException("Trade not found: " + tradeId));

        if (trade.getTradeStatus() != TradeStatus.pending) {
            throw new TradeException("Only pending trades can be declined");
        }

        // Validate listing owner
        ListingResponse listing;
        try {
            listing = listingServiceClient.getListing(trade.getListingId());
        } catch (Exception e) {
            log.error("Failed to fetch listing: {}", trade.getListingId(), e);
            throw new ResourceNotFoundException("Listing not found: " + trade.getListingId());
        }

        if (!listing.getOwnerUserId().equals(listingOwnerId)) {
            throw new TradeException("Only the listing owner can decline trade requests");
        }

        // Update trade status
        trade.setTradeStatus(TradeStatus.rejected);
        Trade savedTrade = tradeRepository.save(trade);

        // Publish trade declined event
        publishTradeEvent("TRADE_DECLINED", savedTrade.getTradeId(), trade.getListingId(), trade.getRequestingUserId());

        log.info("Trade request declined: {}", tradeId);
        return mapToDTO(savedTrade);
    }

    /**
     * Get all trades
     */
    public List<TradeResponseDTO> getAllTrades() {
        log.info("Fetching all trades");
        List<Trade> trades = tradeRepository.findAll();
        return trades.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Get trade by ID
     */
    public TradeResponseDTO getTradeById(Long tradeId) {
        Trade trade = tradeRepository
                .findByIdWithOfferedCards(tradeId)
                .orElseThrow(() -> new ResourceNotFoundException("Trade not found: " + tradeId));
        return mapToDTO(trade);
    }

    /**
     * Get all trades for a listing
     */
    public List<TradeResponseDTO> getTradesByListingId(Long listingId) {
        List<Trade> trades = tradeRepository.findByListingId(listingId);
        return trades.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Get all trades by requesting user
     */
    public List<TradeResponseDTO> getTradesByRequestingUserId(Long userId) {
        List<Trade> trades = tradeRepository.findByRequestingUserId(userId);
        return trades.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Publish trade event to Kafka
     */
    private void publishTradeEvent(String eventType, Long tradeId, Long listingId, Long requestingUserId) {
        try {
            TradeEvent event = new TradeEvent(eventType, tradeId, listingId, requestingUserId);
            kafkaTemplate.send(TRADE_EVENTS_TOPIC, event);
            log.info("Published trade event: {} for trade: {}", eventType, tradeId);
        } catch (Exception e) {
            log.error("Failed to publish trade event", e);
        }
    }

    /**
     * Map Trade entity to DTO
     */
    private TradeResponseDTO mapToDTO(Trade trade) {
        TradeResponseDTO dto = new TradeResponseDTO();
        dto.setTradeId(trade.getTradeId());
        dto.setListingId(trade.getListingId());
        dto.setRequestingUserId(trade.getRequestingUserId());
        dto.setTradeStatus(trade.getTradeStatus());
        dto.setCreatedAt(trade.getCreatedAt());

        if (trade.getOfferedCards() != null) {
            dto.setOfferedCardIds(trade.getOfferedCards().stream()
                    .map(TradeOfferedCard::getCardId)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    /**
     * Trade event for Kafka
     */
    public static class TradeEvent {
        private String eventType;
        private Long tradeId;
        private Long listingId;
        private Long requestingUserId;
        private Long timestamp;

        public TradeEvent() {
            this.timestamp = System.currentTimeMillis();
        }

        public TradeEvent(String eventType, Long tradeId, Long listingId, Long requestingUserId) {
            this.eventType = eventType;
            this.tradeId = tradeId;
            this.listingId = listingId;
            this.requestingUserId = requestingUserId;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters and setters
        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public Long getTradeId() {
            return tradeId;
        }

        public void setTradeId(Long tradeId) {
            this.tradeId = tradeId;
        }

        public Long getListingId() {
            return listingId;
        }

        public void setListingId(Long listingId) {
            this.listingId = listingId;
        }

        public Long getRequestingUserId() {
            return requestingUserId;
        }

        public void setRequestingUserId(Long requestingUserId) {
            this.requestingUserId = requestingUserId;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
