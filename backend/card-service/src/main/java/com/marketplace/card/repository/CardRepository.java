// Generated with assistance from Antigravity through Gemini
// Reviewed and modified by Liam Ruiz
package com.marketplace.card.repository;

import com.marketplace.card.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Card entities.
 * Extends JpaRepository to provide standard CRUD operations.
 * Includes custom query methods for finding cards by name or set ID.
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    /**
     * Finds cards where the name contains the given string (case-insensitive).
     *
     * @param name Name substring to search for.
     * @return List of matching Cards.
     */
    List<Card> findByNameContainingIgnoreCase(String name);

    /**
     * Finds all cards belonging to a specific Set ID.
     *
     * @param setId The Set ID.
     * @return List of Cards in the set.
     */
    List<Card> findBySetId(Integer setId);
}
