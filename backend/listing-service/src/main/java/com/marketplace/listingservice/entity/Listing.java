// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright

package com.marketplace.listingservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a card listing in the marketplace.
 * Users can create listings to trade their cards with other users.
 */
@Entity
@Table(name = "listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "listingId")
    private Long listingId;

    @NotNull(message = "Owner user ID is required")
    @Column(name = "ownerUserId", nullable = false)
    private Long ownerUserId;

    @NotNull(message = "Card ID is required")
    @Column(name = "cardId", nullable = false)
    private Long cardId;

    @Min(value = 1, message = "Condition rating must be at least 1")
    @Max(value = 10, message = "Condition rating must be at most 10")
    @Column(name = "condition_rating")
    private Integer conditionRating;

    @Column(name = "listingStatus", length = 50)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ListingStatus listingStatus = ListingStatus.ACTIVE;

    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (listingStatus == null) {
            listingStatus = ListingStatus.ACTIVE;
        }
    }
}
