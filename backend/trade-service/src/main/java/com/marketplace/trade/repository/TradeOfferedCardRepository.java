// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.repository;

import com.marketplace.trade.model.TradeOfferedCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for TradeOfferedCard entity
 */
@Repository
public interface TradeOfferedCardRepository extends JpaRepository<TradeOfferedCard, Long> {
    
    List<TradeOfferedCard> findByTrade_TradeId(Long tradeId);
    
    void deleteByTrade_TradeId(Long tradeId);
}
