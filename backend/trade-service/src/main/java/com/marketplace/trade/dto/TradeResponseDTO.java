// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.dto;

import com.marketplace.trade.model.Trade.TradeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for trade response
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradeResponseDTO {

    private Long tradeId;
    private Long listingId;
    private Long requestingUserId;
    private TradeStatus tradeStatus;
    private LocalDateTime createdAt;
    private List<Long> offeredCardIds;
}
