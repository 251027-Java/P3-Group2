// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.controller;

import com.marketplace.trade.dto.TradeRequestDTO;
import com.marketplace.trade.dto.TradeResponseDTO;
import com.marketplace.trade.service.TradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for trade operations
 */
@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Trade Controller", description = "API endpoints for managing trade requests")
public class TradeController {

    private final TradeService tradeService;

    @PostMapping
    @Operation(summary = "Create a trade request", description = "Initiate a new trade request for a listing")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "201", description = "Trade request created successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid request"),
                @ApiResponse(responseCode = "404", description = "Listing or user not found"),
                @ApiResponse(responseCode = "409", description = "Trade request already exists")
            })
    public ResponseEntity<TradeResponseDTO> createTradeRequest(@Valid @RequestBody TradeRequestDTO request) {
        log.info("Received request to create trade for listing: {}", request.getListingId());
        TradeResponseDTO response = tradeService.createTradeRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all trades", description = "Retrieve all trade requests in the system")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Trades retrieved successfully")})
    public ResponseEntity<List<TradeResponseDTO>> getAllTrades() {
        log.info("Received request to retrieve all trades");
        List<TradeResponseDTO> trades = tradeService.getAllTrades();
        return ResponseEntity.ok(trades);
    }

    @PutMapping("/{tradeId}/accept")
    @Operation(summary = "Accept a trade request", description = "Accept an incoming trade request for a listing")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Trade request accepted successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid request"),
                @ApiResponse(responseCode = "404", description = "Trade not found"),
                @ApiResponse(responseCode = "403", description = "Not authorized to accept this trade")
            })
    public ResponseEntity<TradeResponseDTO> acceptTradeRequest(
            @Parameter(description = "Trade ID to accept") @PathVariable Long tradeId,
            @Parameter(description = "ID of the listing owner") @RequestParam Long listingOwnerId) {
        log.info("Received request to accept trade: {}", tradeId);
        TradeResponseDTO response = tradeService.acceptTradeRequest(tradeId, listingOwnerId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{tradeId}/decline")
    @Operation(summary = "Decline a trade request", description = "Decline an incoming trade request for a listing")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Trade request declined successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid request"),
                @ApiResponse(responseCode = "404", description = "Trade not found"),
                @ApiResponse(responseCode = "403", description = "Not authorized to decline this trade")
            })
    public ResponseEntity<TradeResponseDTO> declineTradeRequest(
            @Parameter(description = "Trade ID to decline") @PathVariable Long tradeId,
            @Parameter(description = "ID of the listing owner") @RequestParam Long listingOwnerId) {
        log.info("Received request to decline trade: {}", tradeId);
        TradeResponseDTO response = tradeService.declineTradeRequest(tradeId, listingOwnerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tradeId}")
    @Operation(summary = "Get trade by ID", description = "Retrieve a specific trade by its ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Trade found"),
                @ApiResponse(responseCode = "404", description = "Trade not found")
            })
    public ResponseEntity<TradeResponseDTO> getTradeById(
            @Parameter(description = "Trade ID") @PathVariable Long tradeId) {
        TradeResponseDTO response = tradeService.getTradeById(tradeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/listing/{listingId}")
    @Operation(summary = "Get trades by listing ID", description = "Retrieve all trades for a specific listing")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Trades retrieved successfully")})
    public ResponseEntity<List<TradeResponseDTO>> getTradesByListingId(
            @Parameter(description = "Listing ID") @PathVariable Long listingId) {
        List<TradeResponseDTO> trades = tradeService.getTradesByListingId(listingId);
        return ResponseEntity.ok(trades);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get trades by user ID", description = "Retrieve all trades initiated by a specific user")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Trades retrieved successfully")})
    public ResponseEntity<List<TradeResponseDTO>> getTradesByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        List<TradeResponseDTO> trades = tradeService.getTradesByRequestingUserId(userId);
        return ResponseEntity.ok(trades);
    }
}
