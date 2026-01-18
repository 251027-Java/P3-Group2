// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright

package com.marketplace.listingservice.entity;

/**
 * Enum representing the possible statuses of a listing.
 */
public enum ListingStatus {
    ACTIVE("active"),
    COMPLETED("completed"),
    CANCELLED("cancelled");

    private final String value;

    ListingStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ListingStatus fromValue(String value) {
        for (ListingStatus status : ListingStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown listing status: " + value);
    }
}
