// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "trades", schema = "trade_management")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tradeId")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long tradeId;

    @Column(name = "listingId", nullable = false)
    @ToString.Include
    private Long listingId;

    @Column(name = "requestingUserId", nullable = false)
    @ToString.Include
    private Long requestingUserId;

    @Column(name = "listingOwnerUserId", nullable = false)
    @ToString.Include
    private Long listingOwnerUserId;

    @Column(name = "tradeStatus", nullable = false, length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'pending'")
    @Enumerated(EnumType.STRING)
    @ToString.Include
    private TradeStatus tradeStatus;

    @Column(
            name = "createdAt",
            nullable = false,
            updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TradeOfferedCard> offeredCards;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (tradeStatus == null) tradeStatus = TradeStatus.pending;
    }

    public enum TradeStatus {
        pending,
        accepted,
        rejected,
        cancelled
    }
}
