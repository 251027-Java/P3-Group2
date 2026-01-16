// Generated with assistance from Antigravity
// Reviewed and modified by Liam Ruiz
package com.marketplace.card.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entity representing a Trading Card.
 * Maps to the 'cards' table in the database.
 * Stores essential card information including name, image, sets, and market price.
 */
@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @Column(name = "card_id")
    private Long cardId; // Mapped from productId

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "clean_name")
    private String cleanName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "set_id")
    private Integer setId; // Originally groupId

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "price")
    private BigDecimal price; // Market Price

    // You can add more fields from extendedData if needed (e.g. Rarity, Number)
}
