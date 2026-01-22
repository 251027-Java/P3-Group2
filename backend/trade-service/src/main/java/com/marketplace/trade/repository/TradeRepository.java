// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.repository;

import com.marketplace.trade.model.Trade;
import com.marketplace.trade.model.Trade.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Trade entity
 */
@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByListingId(Long listingId);

    List<Trade> findByRequestingUserId(Long requestingUserId);

    List<Trade> findByListingIdAndTradeStatus(Long listingId, TradeStatus status);

    List<Trade> findByRequestingUserIdAndTradeStatus(Long requestingUserId, TradeStatus status);

    @Query(
            "SELECT t FROM Trade t WHERE t.listingId = :listingId AND t.requestingUserId = :requestingUserId AND t.tradeStatus = 'pending'")
    Optional<Trade> findPendingTradeByListingAndUser(
            @Param("listingId") Long listingId, @Param("requestingUserId") Long requestingUserId);

    @Query("SELECT t FROM Trade t JOIN FETCH t.offeredCards WHERE t.tradeId = :tradeId")
    Optional<Trade> findByIdWithOfferedCards(@Param("tradeId") Long tradeId);

    @Query("""
    SELECT t FROM Trade t
    WHERE t.listingId IN (
            SELECT l.listingId FROM Listing l WHERE l.ownerUserId = :userId
    )
    """)
    List<Trade> findTradesByListingOwnerUserId(Long userId);

}
