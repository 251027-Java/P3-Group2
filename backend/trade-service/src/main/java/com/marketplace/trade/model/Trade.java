// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a trade request
 */
@Entity
@Table(name = "trades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tradeId")
    private Long tradeId;
    
    @Column(name = "listingId", nullable = false)
    private Long listingId;
    
    @Column(name = "requestingUserId", nullable = false)
    private Long requestingUserId;
    
    @Column(name = "tradeStatus", nullable = false, length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'pending'")
    @Enumerated(EnumType.STRING)
    private TradeStatus tradeStatus;
    
    @Column(name = "createdAt", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TradeOfferedCard> offeredCards;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (tradeStatus == null) {
            tradeStatus = TradeStatus.pending;
        }
    }
    
    public enum TradeStatus {
        pending,
        accepted,
        rejected,
        cancelled
    }
}
