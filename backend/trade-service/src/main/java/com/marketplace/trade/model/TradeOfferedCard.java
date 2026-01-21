// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing cards offered in a trade
 */
@Entity
@Table(name = "TradeOfferedCards", schema = "trade_management")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class TradeOfferedCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "offeredId")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long offeredId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tradeId", nullable = false)
    private Trade trade;

    @Column(name = "cardId", nullable = false)
    @ToString.Include
    private Long cardId;
}
