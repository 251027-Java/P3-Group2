// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing cards offered in a trade
 */
@Entity
@Table(name = "TradeOfferedCards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradeOfferedCard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "offeredId")
    private Long offeredId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tradeId", nullable = false)
    private Trade trade;
    
    @Column(name = "cardId", nullable = false)
    private Long cardId;
}
